package com.nkk.Order.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private double totalAmount;
    private String orderStatus;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;
}
