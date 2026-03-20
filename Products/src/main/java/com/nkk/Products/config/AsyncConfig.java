package com.nkk.Products.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "productsIoExecutor")
    public ThreadPoolTaskExecutor productsIoExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(20);        // start threads
        exec.setMaxPoolSize(80);         // maximum threads
        exec.setQueueCapacity(2000);     // bounded queue for backpressure
        exec.setKeepAliveSeconds(60);
        exec.setThreadNamePrefix("prod-io-");
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.initialize();
        return exec;
    }
    @Bean(name = "cpuExecutor")
    public ThreadPoolTaskExecutor cpuExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(8);
        exec.setMaxPoolSize(32);
        exec.setQueueCapacity(500);
        exec.setThreadNamePrefix("cpu-");
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.initialize();
        return exec;
    }

    @Bean(name = "ioExecutor")
    public ThreadPoolTaskExecutor ioExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(20);
        exec.setMaxPoolSize(100);
        exec.setQueueCapacity(2000);
        exec.setThreadNamePrefix("io-");
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.initialize();
        return exec;
    }

    @Bean(name = "indexExecutor")
    public ThreadPoolTaskExecutor indexExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(4);
        exec.setMaxPoolSize(16);
        exec.setQueueCapacity(500);
        exec.setThreadNamePrefix("index-");
        exec.initialize();
        return exec;
    }

    @Bean(name = "notifyExecutor")
    public ThreadPoolTaskExecutor notifyExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(6);
        exec.setMaxPoolSize(24);
        exec.setQueueCapacity(1000);
        exec.setThreadNamePrefix("notify-");
        exec.initialize();
        return exec;
    }
}
