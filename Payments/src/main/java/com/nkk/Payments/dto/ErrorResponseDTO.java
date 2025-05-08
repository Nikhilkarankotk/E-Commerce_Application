package com.nkk.Payments.dto;

import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data @AllArgsConstructor
public class ErrorResponseDTO {
    private String apiPath;
    private String errorMessage;
    private HttpStatus errorStatus;
    private LocalDateTime errorTime;
}
