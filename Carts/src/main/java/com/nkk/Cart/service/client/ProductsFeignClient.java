package com.nkk.Cart.service.client;

import com.nkk.Cart.Dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("Products")
public interface ProductsFeignClient {

    @GetMapping("/products/{id}")
    public ProductDTO getProductById(@PathVariable Long id);
}
