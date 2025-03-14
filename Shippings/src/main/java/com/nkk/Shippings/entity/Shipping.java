package com.nkk.Shippings.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shippingId;
    private Long orderId;
    private String shippingAddress;
    private String status; // e.g., PROCESSING, SHIPPED, DELIVERED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
