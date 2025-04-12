package com.nkk.Order.service.client;

import com.nkk.Order.dto.OrderEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Message", url = "http://localhost:9060")
public interface MessagingFeignClient {
    @PostMapping("/publishOrderCreatedEvent")
    ResponseEntity<Void> publishOrderCreatedEvent(@RequestBody OrderEvent orderEvent);
}
