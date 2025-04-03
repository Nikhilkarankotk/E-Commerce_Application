//package com.nkk.gatewayServer.filter;
//import io.lettuce.core.RedisClient;
//import io.lettuce.core.api.StatefulRedisConnection;
//import io.lettuce.core.api.sync.RedisCommands;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CustomRedisRateLimiter extends AbstractGatewayFilterFactory<CustomRedisRateLimiter.Config> {
//    private static final String RATE_LIMIT_KEY = "rate_limit";
//    private static final int MAX_REQUESTS = 4; // Max requests per minute
//    private static final int TIME_WINDOW = 60; // Time window in seconds
//    private final RedisCommands<String, String> redisCommands;
//    public CustomRedisRateLimiter() {
//        super(Config.class);
//        // Initialize Redis connection
//        RedisClient redisClient = RedisClient.create("redis://localhost:6379");
//        StatefulRedisConnection<String, String> connection = redisClient.connect();
//        this.redisCommands = connection.sync();
//    }
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            String ipAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
//            String key = RATE_LIMIT_KEY + ":" + ipAddress;
//            // Get current request count from Redis
//            Long currentRequests = redisCommands.incr(key);
//            // Set expiration for the key if it's the first request
//            if (currentRequests == 1) {
//                redisCommands.expire(key, TIME_WINDOW);
//            }
//            // Check if rate limit is exceeded
//            if (currentRequests > MAX_REQUESTS) {
//                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS);
//                return exchange.getResponse().setComplete();
//            }
//            // Proceed with the request
//            return chain.filter(exchange);
//        };
//    }
//    public static class Config {
//        // Configuration properties (if needed)
//    }
//}
