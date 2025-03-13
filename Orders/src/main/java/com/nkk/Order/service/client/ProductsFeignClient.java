package com.nkk.Order.service.client;

import com.nkk.Order.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient(name = "Products")
public interface ProductsFeignClient {

    @PutMapping("/products/stock")
    ProductDTO updateProductStock(@RequestBody ProductDTO productDTO);

    @GetMapping("/products/{id}")
    public ProductDTO getProductById(@PathVariable Long id);

}
