package com.nkk.Order.service.client;

import com.nkk.Order.dto.CartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "Carts")
public interface CartsFeignClient {

    @GetMapping("/cart")
    CartDTO getCartByUserId(@RequestHeader("Authorization") String token);
}
