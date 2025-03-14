package com.nkk.Payments.mapper;

import com.nkk.Payments.dto.PaymentDTO;
import com.nkk.Payments.entity.Payment;
import org.springframework.stereotype.Component;
@Component
public class PaymentMapper {
    public PaymentDTO mapToPaymentDTO(Payment payment) {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentId(payment.getPaymentId());
        paymentDTO.setOrderId(payment.getOrderId());
        paymentDTO.setAmount(payment.getAmount());
        paymentDTO.setPaymentMethod(payment.getPaymentMethod());
        paymentDTO.setStatus(payment.getStatus());
        paymentDTO.setCreatedAt(payment.getCreatedAt());
        return paymentDTO;
    }
    public Payment mapToPayment(PaymentDTO paymentDTO) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentDTO.getPaymentId());
        payment.setOrderId(paymentDTO.getOrderId());
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setStatus(paymentDTO.getStatus());
        payment.setCreatedAt(paymentDTO.getCreatedAt());
        return payment;
    }
}
