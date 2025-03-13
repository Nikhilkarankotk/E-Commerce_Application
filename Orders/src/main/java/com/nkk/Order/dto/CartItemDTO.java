package com.nkk.Order.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long cartItemId;
    private Long productId;
    private String productName; // Added for better API responses
    private Double productPrice; // Added for better API responses
    private Integer quantity;
    private Double totalPrice;
}
