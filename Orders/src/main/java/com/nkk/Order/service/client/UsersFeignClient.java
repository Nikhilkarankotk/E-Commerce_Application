package com.nkk.Order.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Users")
public interface UsersFeignClient {
    @GetMapping("/users/email")
    ResponseEntity<Long> getUserIdByEmail(@RequestParam String email);
}
