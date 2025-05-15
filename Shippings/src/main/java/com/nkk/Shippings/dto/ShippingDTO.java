package com.nkk.Shippings.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ShippingDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shippingId;
    private Long orderId;
    private String shippingAddress;
    private String trackingNumber;
    @Enumerated(EnumType.STRING)
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
