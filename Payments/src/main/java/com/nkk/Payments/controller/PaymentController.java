package com.nkk.Payments.controller;

import com.nkk.Payments.dto.CreatePaymentIntentRequestDTO;
import com.nkk.Payments.dto.PaymentDTO;
import com.nkk.Payments.service.IPaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    private IPaymentService paymentService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

//    @PostMapping("/create-intent")
//    public ResponseEntity<String> createPaymentIntent(
//            @RequestBody Map<String, Object> requestBody) {
//        try {
//            logger.info("Received request: {}", requestBody); // Log the incoming request
//            Long orderId = Long.valueOf(requestBody.get("orderId").toString());
//            double amount = Double.parseDouble(requestBody.get("amount").toString());
//            String paymentIntentId = paymentService.createPaymentIntent(orderId, amount);
//            return new ResponseEntity<>(paymentIntentId, HttpStatus.OK);
//        } catch (Exception e) {
//            logger.error("Failed to process request: ", e); // Log the error
//            return new ResponseEntity<>("Failed to create Payment Intent: " + e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }
//    @PostMapping("/create-intent")
//    public ResponseEntity<String> createPaymentIntent(
//            @RequestParam Long orderId,
//            @RequestParam double amount) {
//        try {
//            String paymentIntentId = String.valueOf(paymentService.createPaymentIntent(orderId, amount));
//            return new ResponseEntity<>(paymentIntentId, HttpStatus.OK);
//        } catch (StripeException e) {
//            return new ResponseEntity<>("Failed to create Payment Intent: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    @PostMapping("/create-intent")
    public ResponseEntity<Map<String,String>> createPaymentIntent(
        @RequestBody CreatePaymentIntentRequestDTO request) {
    try {
        Long orderId = request.getOrderId();
        double amount = request.getAmount();
        String paymentIntentId = paymentService.createPaymentIntent(orderId, amount);
        // Return a JSON object
        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", paymentIntentId);
        return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (Exception e) {
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}
    @PostMapping("/confirm")
    public ResponseEntity<PaymentDTO> confirmPayment(
            @RequestParam String paymentIntentId) {
        try {
            PaymentDTO paymentDTO = paymentService.confirmPayment(paymentIntentId);
            return new ResponseEntity<>(paymentDTO, HttpStatus.OK);
        } catch (StripeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentDTO paymentDTO = paymentService.getPaymentByOrderId(orderId);
        return new ResponseEntity<>(paymentDTO, HttpStatus.OK);
    }
}
