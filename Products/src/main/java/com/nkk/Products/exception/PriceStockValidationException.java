package com.nkk.Products.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PriceStockValidationException extends RuntimeException {
    public PriceStockValidationException(String message) {
        super(message);
    }
}
