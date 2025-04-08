package com.nkk.Cart.controller;

import com.nkk.Cart.Dto.CartDTO;
import com.nkk.Cart.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService cartService;

    @PostMapping("/create")
    public CartDTO addToCart(
            @RequestHeader("Authorization") String token,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        return cartService.addToCart(token, productId, quantity);
    }

    @GetMapping
    public CartDTO getCartByUserId(@RequestHeader("Authorization") String token) {
        return cartService.getCartByUserId(token);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@RequestHeader("Authorization") String token) {
        cartService.clearCart(token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
   