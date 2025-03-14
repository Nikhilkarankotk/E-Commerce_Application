package com.nkk.Shippings.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double totalPrice;
}
