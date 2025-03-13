package com.nkk.Order.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Long productId;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private Long categoryId;
}
