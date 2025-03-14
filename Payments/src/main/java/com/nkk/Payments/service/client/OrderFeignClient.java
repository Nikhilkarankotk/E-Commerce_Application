package com.nkk.Payments.service.client;

import com.nkk.Payments.dto.OrderDTO;
import com.nkk.Payments.dto.PaymentConfirmationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient(name = "Orders")
public interface OrderFeignClient {

    @GetMapping("/orders/{orderId}")
    ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId);
    @PutMapping("/orders/{orderId}/payment")
    OrderDTO confirmPayment(@PathVariable Long orderId, @RequestBody PaymentConfirmationDTO paymentConfirmationDTO);
}
