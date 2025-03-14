package com.nkk.Shippings.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ShippingDTO {
    private Long shippingId;
    private Long orderId;
    private String shippingAddress;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
