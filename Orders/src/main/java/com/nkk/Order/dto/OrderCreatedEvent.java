package com.nkk.Order.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderCreatedEvent {
    private Long orderId;
    private Long userId;
    private Double totalAmount;
    private String orderStatus;
    private LocalDateTime createdAt;
    private List<OrderItemEvent> items;
}