//package com.nkk.Payments.service.impl;
//
//import com.nkk.Payments.dto.OrderDTO;
//import com.nkk.Payments.dto.PaymentConfirmationDTO;
//import com.nkk.Payments.dto.PaymentDTO;
//import com.nkk.Payments.exception.PaymentMissMatchException;
//import com.nkk.Payments.exception.ResourceNotFoundException;
//import com.nkk.Payments.mapper.PaymentMapper;
//import com.nkk.Payments.repository.PaymentRepository;
//import com.nkk.Payments.service.IPaymentService;
//import com.nkk.Payments.service.client.OrderFeignClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class PaymentServiceImpl implements IPaymentService {
//    @Autowired
//    private PaymentRepository paymentRepository;
//    @Autowired
//    private PaymentMapper paymentMapper;
//    @Autowired
//    private OrderFeignClient orderFeignClient;
//    @Autowired
//    private APIContext apiContext;
//
//    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
//
//    @Transactional
//    public PaymentDTO processPayment(Long orderId, double amount, String paymentMethod, String returnUrl, String cancelUrl) throws PayPalRESTException {
//        // Fetch order details
//        OrderDTO orderDTO = orderFeignClient.getOrderById(orderId).getBody();
//        if (orderDTO == null) {
//            throw new ResourceNotFoundException("Order not found with ID: " + orderId);
//        }
//        // Validate payment amount
//        if (amount <= 0) {
//            throw new PaymentMissMatchException("Payment amount must be greater than zero");
//        } else if (amount > orderDTO.getTotalAmount()) {
//            throw new PaymentMissMatchException("Payment amount cannot exceed the total order amount");
//        }
//        // Process payment with PayPal
//        Payment payment = createPayPalPayment(amount, "USD", paymentMethod, returnUrl, cancelUrl);
//        // Map PayPal payment status to internal status
//        String paymentStatus = payment.getState().equals("approved") ? "SUCCESS" : "FAILED";
//        // Create and save the payment
//        com.nkk.Payments.entity.Payment savedPayment = new com.nkk.Payments.entity.Payment();
//        savedPayment.setOrderId(orderId);
//        savedPayment.setAmount(amount);
//        savedPayment.setPaymentMethod(paymentMethod);
//        savedPayment.setStatus(paymentStatus);
//        savedPayment.setCreatedAt(LocalDateTime.now());
//        com.nkk.Payments.entity.Payment savedPaymentEntity = paymentRepository.save(savedPayment);
//        // Notify the Order Service
//        PaymentConfirmationDTO paymentConfirmationDTO = new PaymentConfirmationDTO();
//        paymentConfirmationDTO.setPaymentStatus(paymentStatus);
//        paymentConfirmationDTO.setPaymentTimestamp(LocalDateTime.now());
//        orderFeignClient.confirmPayment(orderId, paymentConfirmationDTO);
//        return paymentMapper.mapToPaymentDTO(savedPaymentEntity);
//    }
//    public Payment createPayPalPayment(double amount, String currency, String paymentMethod, String returnUrl, String cancelUrl) throws PayPalRESTException {
//        try {
//            // Set payment details
//            Amount paymentAmount = new Amount();
//            paymentAmount.setCurrency(currency);
//            paymentAmount.setTotal(String.format("%.2f", amount));
//            Transaction transaction = new Transaction();
//            transaction.setAmount(paymentAmount);
//            List<Transaction> transactions = new ArrayList<>();
//            transactions.add(transaction);
//            Payer payer = new Payer();
//            payer.setPaymentMethod(paymentMethod);
//            RedirectUrls redirectUrls = new RedirectUrls();
//            redirectUrls.setReturnUrl(returnUrl);
//            redirectUrls.setCancelUrl(cancelUrl);
//            Payment payment = new Payment();
//            payment.setIntent("sale");
//            payment.setPayer(payer);
//            payment.setTransactions(transactions);
//            payment.setRedirectUrls(redirectUrls);
//            // Create the payment
//            Payment createdPayment = payment.create(apiContext);
//            logger.info("PayPal payment created: " + createdPayment.getId());
//            return createdPayment;
//        } catch (PayPalRESTException e) {
//            logger.error("Failed to create PayPal payment", e);
////            throw new RuntimeException("Failed to process PayPal payment: " + e.getMessage());
//            throw e;
//        }
//    }
//    public PaymentDTO getPaymentByOrderId(Long orderId) {
//        com.nkk.Payments.entity.Payment payment = paymentRepository.findByOrderId(orderId)
//                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
//        return paymentMapper.mapToPaymentDTO(payment);
//    }
//}
