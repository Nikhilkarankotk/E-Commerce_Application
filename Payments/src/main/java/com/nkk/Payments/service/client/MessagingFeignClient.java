package com.nkk.Payments.service.client;

import com.nkk.Payments.dto.PaymentConfirmedEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Message", url = "http://localhost:9060")
public interface MessagingFeignClient {
    @PostMapping("/publishPaymentConfirmedEvent")
    ResponseEntity<Void> publishPaymentConfirmedEvent(@RequestBody PaymentConfirmedEvent paymentConfirmedEvent);
}
