package com.nkk.Products.service.impl;

import com.nkk.Products.entity.SubCategory;
import com.nkk.Products.exception.PriceStockValidationException;
import com.nkk.Products.exception.ResourceAlreadyExistsException;
import com.nkk.Products.mapper.ProductMapper;
import com.nkk.Products.repository.SubCategoryRepository;
import com.nkk.Products.service.IProductService;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import com.nkk.Products.dto.ProductDTO;
import com.nkk.Products.entity.Product;
import com.nkk.Products.exception.ResourceNotFoundException;
import com.nkk.Products.repository.ProductRepository;


@Service
public class ProductServiceImpl implements IProductService {
    private final SubCategoryRepository subCategoryRepository;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final ReentrantReadWriteLock inventoryLock = new ReentrantReadWriteLock();
    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ThreadPoolTaskExecutor inventoryUpdateThreadPool;
    private final ThreadPoolTaskExecutor bulkProductThreadPool;

    public ProductServiceImpl(SubCategoryRepository subCategoryRepository, ProductMapper productMapper,
                              ProductRepository productRepository,
                              @Qualifier("inventoryUpdateThreadPool") ThreadPoolTaskExecutor inventoryUpdateThreadPool,
                              @Qualifier("bulkProductThreadPool") ThreadPoolTaskExecutor bulkProductThreadPool) {
        this.subCategoryRepository = subCategoryRepository;
        this.productMapper = productMapper;
        this.productRepository = productRepository;
        this.inventoryUpdateThreadPool = inventoryUpdateThreadPool;
        this.bulkProductThreadPool = bulkProductThreadPool;
    }

    @Override
    @Transactional
    public ProductDTO updateProductStock(ProductDTO productDTO) {
        logger.info("Starting stock update for product ID: {} on thread: {}",
                productDTO.getProductId(), Thread.currentThread().getName());

        return CompletableFuture.supplyAsync(() -> {
            logger.info("Processing stock update for product ID: {} on thread: {}",
                    productDTO.getProductId(), Thread.currentThread().getName());

            inventoryLock.writeLock().lock();
            try {
                Product product = productRepository.findById(productDTO.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productDTO.getProductId()));

                int updatedStock = product.getStockQuantity() + productDTO.getStockQuantity();
                product.setStockQuantity(updatedStock);
                Product savedProduct = productRepository.save(product);
                logger.info("Completed stock update for product ID: {}. New stock: {}",
                        productDTO.getProductId(), updatedStock);
                return mapToDTO(savedProduct);
            } finally {
                inventoryLock.writeLock().unlock();
            }
        }, inventoryUpdateThreadPool).join();
    }
    @Override
    public List<ProductDTO> getAllProducts() {
        logger.info("Fetching all products on thread: {}", Thread.currentThread().getName());

        return CompletableFuture.supplyAsync(() -> {
            logger.info("Processing product fetch on thread: {}", Thread.currentThread().getName());

            List<Product> products = productRepository.findAll();
            logger.info("Fetched {} products", products.size());

            return products.stream()
                    .map(ProductMapper::mapToProductDTO)
                    .collect(Collectors.toList());
        }, bulkProductThreadPool).join();
    }


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
    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductMapper::mapToProductDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }
    @Override
    public Integer getProductByStock(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return product.getStockQuantity();
    }
    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStockQuantity(productDTO.getStockQuantity());
        Product savedProduct = productRepository.save(existingProduct);
        return mapToDTO(savedProduct);
    }
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
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

    @PreDestroy
    public void destroy() {
        inventoryUpdateThreadPool.shutdown();
        bulkProductThreadPool.shutdown();
        try {
            if (!inventoryUpdateThreadPool.getThreadPoolExecutor().awaitTermination(60, TimeUnit.SECONDS)) {
                inventoryUpdateThreadPool.getThreadPoolExecutor().shutdownNow();
            }
            if (!bulkProductThreadPool.getThreadPoolExecutor().awaitTermination(60, TimeUnit.SECONDS)) {
                bulkProductThreadPool.getThreadPoolExecutor().shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            inventoryUpdateThreadPool.getThreadPoolExecutor().shutdownNow();
            bulkProductThreadPool.getThreadPoolExecutor().shutdownNow();
        }
    }

    @Scheduled(fixedRate = 5000)
    public void logThreadPoolMetrics() {
        ThreadPoolExecutor inventoryExecutor = inventoryUpdateThreadPool.getThreadPoolExecutor();
        ThreadPoolExecutor bulkExecutor = bulkProductThreadPool.getThreadPoolExecutor();
        logger.info("Inventory ThreadPool - Active: {}, Queue: {}",
                inventoryExecutor.getActiveCount(),
                inventoryExecutor.getQueue().size());
        logger.info("Bulk Product ThreadPool - Active: {}, Queue: {}",
                bulkExecutor.getActiveCount(),
                bulkExecutor.getQueue().size());
    }


}
