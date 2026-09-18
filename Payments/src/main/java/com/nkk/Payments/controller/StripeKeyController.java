package com.nkk.Payments.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stripe")
public class StripeKeyController {

    // Defaults to empty so builds / context-load tests succeed without keys;
    // Stripe publishes the public key to clients at runtime via env var.
    @Value("${STRIPE_PUBLIC_KEY:}")
    private String publishableKey;

    @GetMapping("/public-key")
    public ResponseEntity<String> getPublicKey() {
        return ResponseEntity.ok(publishableKey);
    }
}