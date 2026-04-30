package com.nkk.Order.service.broker;

import com.nkk.Order.dto.OrderCreatedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class OrderEventProducer {

    @Bean
    public Supplier<OrderCreatedEvent> orderCreatedSupplier() {
        // This supplier is never called directly; it only defines the binding.
        // Events are sent via StreamBridge.
        return () -> null;
    }
}