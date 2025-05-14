package com.nkk.Payments.dto;

import com.stripe.model.PaymentIntent;
import lombok.Data;

@Data
public class ConfirmPaymentRequestDTO {
    private String paymentIntentId;
    // Getter and Setter
}
