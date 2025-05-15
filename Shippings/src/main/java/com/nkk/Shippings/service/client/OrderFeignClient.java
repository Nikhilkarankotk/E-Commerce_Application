package com.nkk.Shippings.service.client;

import com.nkk.Shippings.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Orders")
public interface OrderFeignClient {
    @GetMapping("/orders/{orderId}")
    OrderDTO getOrderById(@PathVariable Long orderId);
//    @GetMapping("/orderStatus/{orderId}")
//    String orderStatusById(@RequestParam Long orderId);
}
