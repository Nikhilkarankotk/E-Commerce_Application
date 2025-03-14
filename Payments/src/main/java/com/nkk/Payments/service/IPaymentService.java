package com.nkk.Payments.service;

import com.nkk.Payments.dto.PaymentDTO;

public interface IPaymentService {
    /**
     *
     * @param orderId
     * @param amount
     * @param paymentMethod
     * @return
     */
    PaymentDTO processPayment(Long orderId, double amount, String paymentMethod);

    /**
     *
     * @param orderId
     * @return
     */
    PaymentDTO getPaymentByOrderId(Long orderId);
}
