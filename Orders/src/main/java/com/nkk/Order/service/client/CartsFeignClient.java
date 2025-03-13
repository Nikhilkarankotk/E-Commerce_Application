package com.nkk.Order.service.client;

import com.nkk.Order.dto.CartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "Carts")
public interface CartsFeignClient {

    @GetMapping("cart/{userId}")
    public CartDTO getCartByUserId(@PathVariable Long userId);
}
