package com.nkk.Products.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ParentCategoryNotFoundException extends RuntimeException {

    public ParentCategoryNotFoundException(String responseName) {
        super(responseName);
    }
}
