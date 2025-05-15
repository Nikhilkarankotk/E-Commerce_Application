package com.nkk.Payments.service.impl;

import com.nkk.Payments.dto.OrderDTO;
import com.nkk.Payments.dto.PaymentConfirmationDTO;
import com.nkk.Payments.dto.PaymentDTO;
import com.nkk.Payments.entity.Payment;
import com.nkk.Payments.mapper.PaymentMapper;
import com.nkk.Payments.repository.PaymentRepository;
import com.nkk.Payments.service.IPaymentService;
import com.nkk.Payments.service.client.OrderFeignClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements IPaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private OrderFeignClient orderFeignClient;

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Transactional
    public Map<String, String> createPaymentIntent(Long orderId) throws StripeException {
        OrderDTO orderDTO =orderFeignClient.getOrderById(orderId).getBody();
        if(orderDTO==null) throw new RuntimeException("Order not found with ID: "+ orderId);

        double amount = orderDTO.getTotalAmount();
        if(amount <=0){
            throw new RuntimeException("Payment amount must be greater than zero");
        }
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount((long) (amount * 100))
            .setCurrency("usd")
            .setDescription("Payment for order " + orderId)
            .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .build()
            )
            .build();

    PaymentIntent paymentIntent = PaymentIntent.create(params);

    Map<String, String> response = new HashMap<>();
    response.put("clientSecret", paymentIntent.getClientSecret());
    response.put("paymentIntentId", paymentIntent.getId()); // ✅ also return ID
    return response;
    }

    @Transactional
    public PaymentDTO confirmPayment(String paymentIntentId) {
        try {
            logger.info("Confirming payment with PaymentIntent ID: {}", paymentIntentId);
            // Retrieve the Payment Intent
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            logger.info("Retrieved PaymentIntent status: {}", paymentIntent.getStatus());
            // Map Payment Intent status to payment status
            String paymentStatus = paymentIntent.getStatus().equals("succeeded") ? "SUCCESS" : "FAILED";
            logger.info("Mapped payment status: {}", paymentStatus);
            // Create and save the payment
            Payment payment = new Payment();
            payment.setOrderId(Long.parseLong(paymentIntent.getDescription().split(" ")[3]));
            payment.setAmount(paymentIntent.getAmount() / 100.0);
            payment.setPaymentMethod("card");
            payment.setStatus(paymentStatus);
            payment.setCreatedAt(LocalDateTime.now());
            Payment savedPayment = paymentRepository.save(payment);
            logger.info("Payment saved: {}", savedPayment);
            // Notify the Order Service
            PaymentConfirmationDTO paymentConfirmationDTO = new PaymentConfirmationDTO();
            paymentConfirmationDTO.setPaymentStatus(paymentStatus);
            paymentConfirmationDTO.setPaymentTimestamp(LocalDateTime.now());
            orderFeignClient.confirmPayment(savedPayment.getOrderId(), paymentConfirmationDTO);
            logger.info("Order service notified of payment confirmation");
            return paymentMapper.mapToPaymentDTO(savedPayment);
        } catch (StripeException e) {
            logger.error("Failed to confirm payment: ", e);
            throw new RuntimeException("Failed to confirm payment: " + e.getMessage());
        }
    }

    public PaymentDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
        return paymentMapper.mapToPaymentDTO(payment);
    }
}
