package com.nkk.Products.service.impl;



import com.nkk.Products.dto.ProductDTO;
import com.nkk.Products.entity.Product;
import com.nkk.Products.entity.SubCategory;
import com.nkk.Products.exception.ConcurrencyException;
import com.nkk.Products.exception.PriceStockValidationException;
import com.nkk.Products.exception.ResourceAlreadyExistsException;
import com.nkk.Products.exception.ResourceNotFoundException;
import com.nkk.Products.mapper.ProductMapper;
import com.nkk.Products.repository.ProductRepository;
import com.nkk.Products.repository.SubCategoryRepository;
import com.nkk.Products.service.IProductService;
import com.nkk.Products.service.SkuLockService;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * ProductServiceImpl — all multithreading integration.
 * - per-SKU locking for updates/deletes/reservations
 * - parallel reads for bulk fetch
 * - offload external I/O, indexing, notifications to dedicated executors
 * - structured logging via MDC and timing logs
 */
@Service
public class ProductServiceImpl implements IProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final SkuLockService skuLockService;
    private final ProductMapper productMapper;
    private final Executor productsIoExecutor;
    private final Executor cpuExecutor;
    private final Executor ioExecutor;
    private final Executor indexExecutor;
    private final Executor notifyExecutor;

    private static final long LOCK_TIMEOUT_MS = 3000;
    private static final long TASK_TIMEOUT_MS = 3000;

    public ProductServiceImpl(ProductRepository productRepository, SubCategoryRepository subCategoryRepository,
                              SkuLockService skuLockService, ProductMapper productMapper,
                              @Qualifier("productsIoExecutor") Executor productsIoExecutor,
                              @Qualifier("cpuExecutor") Executor cpuExecutor,
                              @Qualifier("ioExecutor") Executor ioExecutor,
                              @Qualifier("indexExecutor") Executor indexExecutor,
                              @Qualifier("notifyExecutor") Executor notifyExecutor) {
        this.productRepository = productRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.skuLockService = skuLockService;
        this.productMapper = productMapper;
        this.productsIoExecutor = productsIoExecutor;
        this.cpuExecutor = cpuExecutor;
        this.ioExecutor = ioExecutor;
        this.indexExecutor = indexExecutor;
        this.notifyExecutor = notifyExecutor;
    }
    @Override
    public CompletableFuture<List<ProductDTO>> getAllProductsAsync(int page, int size) {
        long overallStart = System.nanoTime();
        log.info("[getAllProducts] Started async retrieval for page={} size={} ...", page, size);

        List<Long> ids = productRepository.findProductIds(PageRequest.of(page, size));
        if (ids == null || ids.isEmpty()) {
            log.warn("⚠ [getAllProducts] No product IDs found for page={} size={}", page, size);
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        // Log how many parallel tasks will run
        log.info("[getAllProducts] Launching {} parallel tasks", ids.size());

        // Parallel product retrieval
        List<CompletableFuture<ProductDTO>> futures = ids.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> {
                            MDC.put("productId", String.valueOf(id));
                            MDC.put("thread", Thread.currentThread().getName());

                            long start = System.nanoTime();
                            log.info("[ThreadStart] Fetching product id={} on thread={}", id, Thread.currentThread().getName());
                            try {
                                // DB call or remote fetch simulation
                                Optional<Product> opt = productRepository.findById(id);
                                if (!opt.isPresent()) {
                                    log.warn("❌ Product id={} not found", id);
                                    return null;
                                }
                                Product product = opt.get();

                                // Map to DTO
                                ProductDTO dto = mapToDTO(product);

                                long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                                log.info("✅ [ThreadComplete] Product id={} fetched in {} ms on thread={}",
                                        id, elapsedMs, Thread.currentThread().getName());
                                return dto;
                            } catch (Exception ex) {
                                log.error("❌[ThreadError] Product id={} failed with {}", id, ex.getMessage(), ex);
                                return null;
                            } finally {
                                MDC.remove("productId");
                                MDC.remove("thread");
                            }
                        }, productsIoExecutor)
                        .orTimeout(TASK_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                        .exceptionally(ex -> {
                            log.error("⏱ [ThreadTimeout] Product task failed/timed out: {}", ex.toString());
                            return null;
                        }))
                .collect(Collectors.toList());

        // Combine results and log summary
        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return all.thenApply(v -> {
            long totalElapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - overallStart);
            List<ProductDTO> results = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            log.info("🏁 [getAllProducts] Completed in {} ms with {} products fetched successfully ({} tasks failed or timed out)",
                    totalElapsed,
                    results.size(),
                    (ids.size() - results.size()));

            return results;
        });
    }

    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductMapper::mapToProductDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }
    public Integer getProductByStock(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return product.getStockQuantity();
    }
    @Transactional
    public ProductDTO addProduct(ProductDTO productDTO) {
        if (productDTO.getSubCategoryId() == null) {
            throw new IllegalArgumentException("SubCategory ID must not be null");
        }

        SubCategory subCategory = subCategoryRepository.findById(productDTO.getSubCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory", "id", productDTO.getSubCategoryId()));

        if (productRepository.existsByNameAndSubCategory_SubCategoryId(productDTO.getName(), productDTO.getSubCategoryId())) {
            throw new ResourceAlreadyExistsException("Product already exists with name: "
                    + productDTO.getName() + " in Subcategory ID: " + productDTO.getSubCategoryId());
        }

        Product product = productMapper.mapToProduct(productDTO);
        product.setSubCategory(subCategory);
        validateProduct(product);

        Product saved = productRepository.save(product);
        return productMapper.mapToProductDTO(saved);
    }

    private void validateProduct(Product product) {
        if (product.getStockQuantity() <= 0) {
            throw new PriceStockValidationException("Stock quantity cannot be negative or zero");
        }
        if (product.getPrice() <= 0) {
            throw new PriceStockValidationException("Price must be greater than 0");
        }
        if (product.getName() == null || product.getName().trim().isEmpty() || product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            throw new PriceStockValidationException("Product name is required and description is required");
        }
        if (product.getSubCategory() == null) {
            throw new PriceStockValidationException("Product category is required");
        }
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) throws ConcurrencyException {
        String skuKey = String.valueOf(id);
        try {
            Product saved = skuLockService.withLock(skuKey, LOCK_TIMEOUT_MS, () -> {
                Product existingProduct = productRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
                existingProduct.setName(productDTO.getName());
                existingProduct.setDescription(productDTO.getDescription());
                existingProduct.setPrice(productDTO.getPrice());
                existingProduct.setStockQuantity(productDTO.getStockQuantity());
                // You might want to validate price/stock here
                validateProduct(existingProduct);
                Product s = productRepository.save(existingProduct);
                log.debug("Product updated under lock: {}", s.getProductId());
                return s;
            });
            return mapToDTO(saved);
        } catch (SkuLockService.LockAcquisitionException le) {
            throw new ConcurrencyException("Could not update product right now, please retry");
        }
    }

    // update single product stock (synchronous)
    @Override
    @Transactional
    public ProductDTO updateProductStock(ProductDTO dto) {
        String key = String.valueOf(dto.getProductId());
        long overallStart = System.nanoTime();

        return skuLockService.withLock(key, LOCK_TIMEOUT_MS, () -> {
            MDC.put("sku", key);
            MDC.put("thread", Thread.currentThread().getName());
            long lockAcquiredAt = System.nanoTime();
            try {
                log.info("Lock acquired; loading product");
                var product = productRepository.findById(dto.getProductId())
                        .orElseThrow(() -> new RuntimeException("product not found " + dto.getProductId()));

                int newStock = product.getStockQuantity() + dto.getStockQuantity();
                if (newStock < 0) {
                    throw new RuntimeException("stock cannot be negative");
                }
                product.setStockQuantity(newStock);
                var saved = productRepository.save(product);

                long lockHeldMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - lockAcquiredAt);
                log.info("Stock updated sku={}, newStock={}, lockHeldMs={}, overallMs={}",
                        key,
                        saved.getStockQuantity(),
                        lockHeldMs,
                        TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - overallStart));

                // async side-effects
                reindexProductAsync(saved.getProductId());
                notifyProductChangeAsync(saved.getProductId());

                return mapToDTO(saved);
            } finally {
                MDC.remove("sku");
                MDC.remove("thread");
            }
        });
    }


    // async wrapper
    @Override
    public CompletableFuture<ProductDTO> updateProductStockAsync(ProductDTO dto) {
        return CompletableFuture.supplyAsync(() -> updateProductStock(dto), cpuExecutor);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // update images (I/O offloaded)
    @Override
    public CompletableFuture<Void> updateProductImagesAsync(Long productId, List<String> imageUrls) {
        return CompletableFuture.runAsync(() -> {
            MDC.put("sku", String.valueOf(productId));
            MDC.put("thread", Thread.currentThread().getName());
            try {
                for (String url : imageUrls) {
                    try {
                        simulateExternalCall(url);
                        log.info("Image updated for product={}, url={}", productId, url);
                    } catch (Exception ex) {
                        log.warn("Image update failed product={}, url={}, err={}", productId, url, ex.getMessage());
                    }
                }
            } finally {
                MDC.remove("sku");
                MDC.remove("thread");
            }
        }, ioExecutor);
    }

    // reindex (indexExecutor)
    @Override
    @Async("indexExecutor")
    public CompletableFuture<Void> reindexProductAsync(Long productId) {
        return CompletableFuture.runAsync(() -> {
            MDC.put("sku", String.valueOf(productId));
            MDC.put("thread", Thread.currentThread().getName());
            long s = System.nanoTime();
            try {
                var p = productRepository.findById(productId).orElse(null);
                if (p == null) {
                    log.warn("reindex: product not found {}", productId);
                    return;
                }
                simulateIndexCall(p);
                log.info("Reindexed product={}, durMs={}", productId, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - s));
            } finally {
                MDC.remove("sku");
                MDC.remove("thread");
            }
        }, indexExecutor);
    }

    // notify (notifyExecutor)
    @Override
    public CompletableFuture<Void> notifyProductChangeAsync(Long productId) {
        return CompletableFuture.runAsync(() -> {
            MDC.put("sku", String.valueOf(productId));
            MDC.put("thread", Thread.currentThread().getName());
            try {
                simulateNotificationSend(productId);
                log.info("Notification triggered for product={}", productId);
            } finally {
                MDC.remove("sku");
                MDC.remove("thread");
            }
        }, notifyExecutor);
    }

    // helpers (replace with real implementations)
    private void simulateExternalCall(String url) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private void simulateIndexCall(Object product) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void simulateNotificationSend(Long productId) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    private ProductDTO mapToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setStockQuantity(product.getStockQuantity());
        return productDTO;
    }
}
