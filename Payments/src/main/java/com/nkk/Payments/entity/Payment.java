package com.nkk.Payments.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    private Long orderId;
    private double amount;
    private String paymentMethod; // e.g., CREDIT_CARD, PAYPAL
    private String status; // e.g., SUCCESS, FAILED
    private LocalDateTime createdAt;
}
