package com.nkk.Shippings.controller;

import com.nkk.Shippings.dto.ShippingDTO;
import com.nkk.Shippings.dto.ShippingStatus;
import com.nkk.Shippings.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/shipping")
public class ShippingController {
    @Autowired
    private IShippingService shippingService;
    @PostMapping
    public ResponseEntity<ShippingDTO> createShipping(
            @RequestParam Long orderId,
            @RequestParam String shippingAddress) {
        ShippingDTO shippingDTO = shippingService.createShipping(orderId, shippingAddress);
        return new ResponseEntity<>(shippingDTO, HttpStatus.CREATED);
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<ShippingDTO> getShippingByOrderId(@PathVariable Long orderId) {
        ShippingDTO shippingDTO = shippingService.getShippingByOrderId(orderId);
        return new ResponseEntity<>(shippingDTO, HttpStatus.OK);
    }
    @PutMapping("/{shippingId}")
    public ResponseEntity<ShippingDTO> updateShippingStatus(
            @PathVariable Long shippingId,
            @RequestParam ShippingStatus status) {
        ShippingDTO shippingDTO = shippingService.updateShippingStatus(shippingId, status);
        return new ResponseEntity<>(shippingDTO, HttpStatus.OK);
    }
}
