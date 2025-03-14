package com.nkk.Payments.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class PaymentDTO {
    private Long paymentId;
    private Long orderId;
    private double amount;
    private String paymentMethod;
    private String status;
    private LocalDateTime createdAt;
}
