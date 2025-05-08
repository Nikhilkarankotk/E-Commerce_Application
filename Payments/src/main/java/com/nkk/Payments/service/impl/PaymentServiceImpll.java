//package com.nkk.Payments.service.impl;
//
//import com.nkk.Payments.dto.OrderDTO;
//import com.nkk.Payments.dto.PaymentConfirmationDTO;
//import com.nkk.Payments.dto.PaymentDTO;
//import com.nkk.Payments.entity.Payment;
//import com.nkk.Payments.exception.PaymentMissMatchException;
//import com.nkk.Payments.exception.ResourceNotFoundException;
//import com.nkk.Payments.mapper.PaymentMapper;
//import com.nkk.Payments.repository.PaymentRepository;
//import com.nkk.Payments.service.IPaymentService;
//import com.nkk.Payments.service.client.OrderFeignClient;
//import com.stripe.exception.StripeException;
//import com.stripe.model.Charge;
//import com.stripe.param.ChargeCreateParams;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//@Service
//public class PaymentServiceImpll implements IPaymentService {
//    @Autowired
//    private PaymentRepository paymentRepository;
//    @Autowired
//    private PaymentMapper paymentMapper;
//    @Autowired
//    private OrderFeignClient orderFeignClient;
//    @Transactional
//    public PaymentDTO processPayment(Long orderId, double amount, String paymentMethod) {
//        // Fetch order details
//        OrderDTO orderDTO = orderFeignClient.getOrderById(orderId).getBody();
//        if (orderDTO == null) {
//            throw new ResourceNotFoundException("Order not found with ID: " + orderId);
//        }
//        // Validate payment amount
//        if(amount != orderDTO.getTotalAmount()) {
//            throw new PaymentMissMatchException("Payment amount must be equal to the total order amount");
//        }else if (amount <= 0) {
//            throw new PaymentMissMatchException("Payment amount must be greater than zero");
//        } else if (amount > orderDTO.getTotalAmount()) {
//            throw new PaymentMissMatchException("Payment amount cannot exceed the total order amount");
//        }
//
//        // Process payment with Stripe
//        Charge charge;
//        try {
//            charge = processPaymentWithStripe(amount, "usd", paymentMethod);
//        } catch (StripeException e) {
//            throw new RuntimeException("Payment failed: " + e.getMessage());
//        }
//        // Map Stripe charge status to payment status
//        String paymentStatus = charge.getStatus().equals("succeeded") ? "SUCCESS" : "FAILED";
//        // Create and save the payment
//        Payment payment = new Payment();
//        payment.setOrderId(orderId);
//        payment.setAmount(amount);
//        payment.setPaymentMethod(paymentMethod);
//        payment.setStatus(paymentStatus);
//        payment.setCreatedAt(LocalDateTime.now());
//        Payment savedPayment = paymentRepository.save(payment);
//        // Notify the Order Service
//        PaymentConfirmationDTO paymentConfirmationDTO = new PaymentConfirmationDTO();
//        paymentConfirmationDTO.setPaymentStatus(paymentStatus);
//        paymentConfirmationDTO.setPaymentTimestamp(LocalDateTime.now());
//        orderFeignClient.confirmPayment(orderId, paymentConfirmationDTO);
//        return paymentMapper.mapToPaymentDTO(savedPayment);
//    }
//    public Charge processPaymentWithStripe(double amount, String currency, String paymentMethod) throws StripeException {
//        ChargeCreateParams params = ChargeCreateParams.builder()
//                .setAmount((long) (amount * 100)) // Amount in cents
//                .setCurrency(currency)
//                .setSource(paymentMethod) // Payment method token
//                .build();
//        return Charge.create(params);
//    }
//    public PaymentDTO getPaymentByOrderId(Long orderId) {
//        com.nkk.Payments.entity.Payment payment = paymentRepository.findByOrderId(orderId)
//                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
//        return paymentMapper.mapToPaymentDTO(payment);
//    }
//}
