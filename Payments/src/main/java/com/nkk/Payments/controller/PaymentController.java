package com.nkk.Payments.controller;

import com.nkk.Payments.dto.ConfirmPaymentRequestDTO;
import com.nkk.Payments.dto.CreatePaymentIntentRequestDTO;
import com.nkk.Payments.dto.PaymentConfirmationDTO;
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

    @PostMapping("/create-intent")
    public ResponseEntity<Map<String,String>> createPaymentIntent(
        @RequestBody CreatePaymentIntentRequestDTO request) {
        try {
            Map<String, String> response = paymentService.createPaymentIntent(request.getOrderId(), request.getAmount());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/confirm")
    public ResponseEntity<PaymentDTO> confirmPayment(@RequestBody ConfirmPaymentRequestDTO request) {
        try {
        return new ResponseEntity<>(paymentService.confirmPayment(request.getPaymentIntentId()), HttpStatus.OK);
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
