package com.nkk.Products.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {
    @Bean(name = "inventoryUpdateThreadPool")
    public ThreadPoolTaskExecutor inventoryUpdateThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);         // Threads for inventory updates
        executor.setMaxPoolSize(50);          // Max threads during peak load
        executor.setQueueCapacity(100);      // Queue size before new threads spawn
        executor.setThreadNamePrefix("inventory-update-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
    @Bean(name = "bulkProductThreadPool")
    public ThreadPoolTaskExecutor bulkProductThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);          // Threads for bulk operations
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("bulk-product-");
        executor.initialize();
        return executor;
    }
}



