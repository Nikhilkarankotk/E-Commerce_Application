package com.nkk.Order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data @AllArgsConstructor
public class ErrorResponseDTO {
    private String apiPath;
    private String errorMessage;
    private HttpStatus errorStatus;
    private LocalDateTime errorTime;
}
