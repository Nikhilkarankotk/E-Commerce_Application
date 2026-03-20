package com.nkk.Products.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConcurrencyException extends Throwable {
    public ConcurrencyException(String message) {
        super(message);
    }
}
