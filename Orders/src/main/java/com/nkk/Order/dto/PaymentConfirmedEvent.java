package com.nkk.Order.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentConfirmedEvent {
    private Long orderId;
    private Long paymentId;
    private String status;   // "SUCCESS" or "FAILED"
    private Double amount;
    private LocalDateTime paymentTimestamp;
}