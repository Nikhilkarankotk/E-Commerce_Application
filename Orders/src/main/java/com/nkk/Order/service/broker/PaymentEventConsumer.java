package com.nkk.Order.service.broker;
import com.nkk.Order.dto.PaymentConfirmedEvent;
import com.nkk.Order.entity.Order;
import com.nkk.Order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class PaymentEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventConsumer.class);

    @Autowired
    private OrderRepository orderRepository;

    @Bean
    public Consumer<PaymentConfirmedEvent> paymentConfirmed() {
        return event -> {
            logger.info("Received PaymentConfirmedEvent for order: {}", event.getOrderId());
            Order order = orderRepository.findById(event.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found: " + event.getOrderId()));

            // Idempotency check
            String currentStatus = order.getOrderStatus();
            if ("PAYMENT_SUCCESS".equals(currentStatus) || "PAYMENT_FAILED".equals(currentStatus)) {
                logger.info("Order {} already has status {}, skipping duplicate", event.getOrderId(), currentStatus);
                return;
            }

            if ("SUCCESS".equals(event.getStatus())) {
                order.setOrderStatus("PAYMENT_SUCCESS");
            } else {
                order.setOrderStatus("PAYMENT_FAILED");
            }
            orderRepository.save(order);
            logger.info("Updated order {} status to {}", order.getOrderId(), order.getOrderStatus());
        };
    }
}