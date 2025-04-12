package com.nkk.Payments.service.impl;

import com.nkk.Payments.dto.OrderDTO;
import com.nkk.Payments.dto.PaymentConfirmationDTO;
import com.nkk.Payments.dto.PaymentDTO;
import com.nkk.Payments.entity.Payment;
import com.nkk.Payments.mapper.PaymentMapper;
import com.nkk.Payments.repository.PaymentRepository;
import com.nkk.Payments.service.IPaymentService;
import com.nkk.Payments.service.client.MessagingFeignClient;
import com.nkk.Payments.service.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements IPaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private MessagingFeignClient messagingFeignClient;
//    @Autowired
//    private StreamBridge streamBridge;
    @Transactional
    public PaymentDTO processPayment(Long orderId, double amount, String paymentMethod) {
        // Fetch order details
        OrderDTO orderDTO = orderFeignClient.getOrderById(orderId).getBody();
        // Validate payment amount
        if (amount != orderDTO.getTotalAmount()) {
            throw new RuntimeException("Payment amount does not match the total order amount");
        }
        // Simulate payment processing
        String paymentStatus = Math.random() > 0.1 ? "SUCCESS" : "FAILED"; // 90% success rate
        // Create a new payment
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(paymentStatus);
        payment.setCreatedAt(LocalDateTime.now());
        // Save the payment
        Payment savedPayment = paymentRepository.save(payment);
        // Notify the Order Service
        PaymentConfirmationDTO paymentConfirmationDTO = new PaymentConfirmationDTO();
        paymentConfirmationDTO.setPaymentStatus(paymentStatus);
        paymentConfirmationDTO.setPaymentTimestamp(LocalDateTime.now());
        orderFeignClient.confirmPayment(orderId, paymentConfirmationDTO);
        // Publish "Payment Confirmed" event via Messaging Service
//        PaymentConfirmedEvent paymentConfirmedEvent = new PaymentConfirmedEvent();
//        paymentConfirmedEvent.setOrderId(orderId);
//        paymentConfirmedEvent.setStatus(paymentStatus);
//        streamBridge.send("paymentConfirmed-out-0", paymentConfirmedEvent);
        // Map to DTO and return
        return paymentMapper.mapToPaymentDTO(savedPayment);
    }

    public PaymentDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
        return paymentMapper.mapToPaymentDTO(payment);
    }
}
