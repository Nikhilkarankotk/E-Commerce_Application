package com.nkk.Payments.service;

import com.nkk.Payments.dto.PaymentDTO;
import com.stripe.exception.StripeException;

import java.util.Map;

public interface IPaymentService {
//    /**
//     *
//     * @param orderId
//     * @param amount
//     * @param paymentMethod
//     * @return
//     */
//    PaymentDTO processPayment(Long orderId, double amount, String paymentMethod);
//
//    /**
//     *
//     * @param amount
//     * @param currency
//     * @param paymentMethod
//     * @return
//     * @throws StripeException
//     */
//    Charge processPaymentWithStripe(double amount, String currency, String paymentMethod) throws StripeException;
        /**
         *
         * @param orderId
         * @return
         */
    PaymentDTO getPaymentByOrderId(Long orderId);

    /**
     * @param orderId
     * @param amount
     * @return
     * @throws StripeException
     */
    Map<String, String> createPaymentIntent(Long orderId, double amount) throws StripeException;

    /**
     *
     * @param paymentIntentId
     * @return
     * @throws StripeException
     */
    PaymentDTO confirmPayment(String paymentIntentId) throws StripeException;



}
