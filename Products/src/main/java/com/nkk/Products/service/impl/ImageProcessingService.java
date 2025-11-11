//package com.nkk.Products.service.impl;
//
//import org.springframework.stereotype.Service;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.beans.factory.annotation.Qualifier;
//import java.util.concurrent.CompletableFuture;
//
//@Service
//public class ImageProcessingService {
//    @Autowired
//    @Qualifier("imageProcessingThreadPool")
//    private ThreadPoolTaskExecutor imageProcessingThreadPool;
//    @Async("imageProcessingThreadPool")
//    public CompletableFuture<String> processImageAsync(String imageUrl) {
//        return CompletableFuture.supplyAsync(() -> {
//            // Simulate image processing (e.g., generate thumbnail)
//            try {
//                Thread.sleep(2000); // Simulate CPU-intensive task
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                throw new RuntimeException("Image processing interrupted", e);
//            }
//            return "Processed image for: " + imageUrl;
//        }, imageProcessingThreadPool);
//    }
//}
//
