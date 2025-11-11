//package com.nkk.Products.service.MultiThreading;
//
//import com.nkk.Products.dto.ProductDTO;
//import com.nkk.Products.entity.Product;
//import com.nkk.Products.exception.ResourceNotFoundException;
//import com.nkk.Products.repository.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class InventorySyncService {
//
//    private static final Logger log = LoggerFactory.getLogger(InventorySyncService.class);
//
//    @Autowired
//    private ThreadPoolTaskExecutor vendorTaskExecutor;
//    @Autowired private ProductRepository productRepository;
//
//    // Example: Vendors or warehouses API URLs
//    private final List<String> vendorEndpoints = List.of(
//        "https://vendorA.com/inventory",
//        "https://vendorB.com/inventory",
//        "https://vendorC.com/inventory"
//    );
//
//    public void syncInventoryFromVendors() {
//        List<CompletableFuture<Void>> futures = vendorEndpoints.stream()
//            .map(endpoint -> CompletableFuture.runAsync(() -> fetchAndUpdateInventory(endpoint), vendorTaskExecutor)
//                .orTimeout(10, TimeUnit.SECONDS)
//                .exceptionally(ex -> {
//                    log.error("Error syncing from {}: {}", endpoint, ex.getMessage());
//                    return null;
//                })
//            ).collect(Collectors.toList());
//
//        // Wait for all to complete
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
//        log.info("✅ All vendor inventory sync tasks completed.");
//    }
//
//    private void fetchAndUpdateInventory(String endpoint) {
//        try {
//            log.info("Fetching inventory from {}", endpoint);
//            // Simulated API call
//            List<ProductDTO> vendorProducts = mockExternalVendorCall(endpoint);
//
//            for (ProductDTO productDTO : vendorProducts) {
//                updateLocalInventory(productDTO);
//            }
//
//            log.info("Updated inventory for vendor: {}", endpoint);
//        } catch (Exception e) {
//            log.error("Error processing inventory from {}: {}", endpoint, e.getMessage());
//        }
//    }
//
//    @Transactional
//    public synchronized void updateLocalInventory(ProductDTO productDTO) {
//        Product product = productRepository.findById(productDTO.getProductId())
//                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productDTO.getProductId()));
//
//        product.setStockQuantity(productDTO.getStockQuantity());
//        productRepository.save(product);
//    }
//
//    private List<ProductDTO> mockExternalVendorCall(String endpoint) throws InterruptedException {
//        Thread.sleep((long) (Math.random() * 2000)); // simulate latency
//        ProductDTO productDTO = new ProductDTO();
//        productDTO.setProductId(1L);
//        productDTO.setName("Product A");
//        productDTO.setDescription("Updated by " + endpoint);
//        productDTO.setPrice(200.0);
//        productDTO.setStockQuantity(10);
//        productDTO.setSubCategoryId(1L);
//        productDTO.setSubCategoryName("Mobiles");
//        return List.of(productDTO);
//    }
//}
