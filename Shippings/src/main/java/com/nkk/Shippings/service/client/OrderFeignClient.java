package com.nkk.Shippings.service.client;

import com.nkk.Shippings.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "Orders")
public interface OrderFeignClient {
    @GetMapping("/orders/{orderId}")
    OrderDTO getOrderById(@PathVariable Long orderId);
}
