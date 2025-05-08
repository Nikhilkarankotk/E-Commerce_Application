package com.nkk.Payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PaymentMissMatchException extends RuntimeException {
    public PaymentMissMatchException(String message) {
        super(message);
    }
}
