package com.nkk.Payments.dto;

import com.stripe.model.PaymentIntent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentConfirmationDTO {
    private String paymentStatus; // e.g., SUCCESS, FAILED
    private LocalDateTime paymentTimestamp;
    // Getters and Setters
}
