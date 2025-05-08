//package com.nkk.Payments.controller;
//
//import com.nkk.Payments.dto.PaymentDTO;
//import com.nkk.Payments.service.IPaymentService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//@RestController
//@RequestMapping("/payments")
//public class PaymentControllerrr {
//    @Autowired
//    private IPaymentService paymentService;
//    @PostMapping
//    public ResponseEntity<PaymentDTO> processPayment(
//            @RequestParam Long orderId,
//            @RequestParam double amount,
//            @RequestParam String paymentMethod) {
//        PaymentDTO paymentDTO = paymentService.processPayment(orderId, amount, paymentMethod);
//        return new ResponseEntity<>(paymentDTO, HttpStatus.CREATED);
//    }
//    @GetMapping("/order/{orderId}")
//    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable Long orderId) {
//        PaymentDTO paymentDTO = paymentService.getPaymentByOrderId(orderId);
//        return new ResponseEntity<>(paymentDTO, HttpStatus.OK);
//    }
//}
