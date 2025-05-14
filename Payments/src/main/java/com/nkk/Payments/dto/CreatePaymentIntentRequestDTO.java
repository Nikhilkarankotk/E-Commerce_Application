package com.nkk.Payments.dto;

import lombok.Data;

@Data
public class CreatePaymentIntentRequestDTO {
    private Long orderId;
//    private double amount;
    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
//    public double getAmount() {
//        return amount;
//    }
//    public void setAmount(double amount) {
//        this.amount = amount;
//    }
}
