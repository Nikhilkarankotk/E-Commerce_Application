package com.nkk.Order.dto;

import lombok.Data;

@Data
public class OrderItemEvent {
    private Long productId;
    private Integer quantity;
    private Double productPrice;
}