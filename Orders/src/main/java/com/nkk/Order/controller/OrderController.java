package com.nkk.Order.controller;

import com.nkk.Order.dto.OrderDTO;
import com.nkk.Order.dto.PaymentConfirmationDTO;
import com.nkk.Order.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private IOrderService orderService;
    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(@RequestParam Long userId) {
        OrderDTO orderDTO = orderService.createOrder(userId);
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@RequestParam Long userId) {
        List<OrderDTO> orderDTOs = orderService.getOrdersByUserId(userId);
        return new ResponseEntity<>(orderDTOs, HttpStatus.OK);
    }
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        OrderDTO orderDTO = orderService.updateOrderStatus(orderId, status);
        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }
    @PutMapping("/{orderId}/payment")
    public ResponseEntity<OrderDTO> confirmPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentConfirmationDTO paymentConfirmationDTO) {
        OrderDTO orderDTO = orderService.confirmPayment(orderId, paymentConfirmationDTO);
        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }

}
