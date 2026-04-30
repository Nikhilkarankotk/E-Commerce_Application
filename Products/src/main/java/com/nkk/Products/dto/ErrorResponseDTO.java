package com.nkk.Products.dto;

import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

public class ErrorResponseDTO {
    private String message;
    private HttpStatus status;
    private String details;
    private LocalDateTime timestamp;

    // ✅ Constructor with arguments
    public ErrorResponseDTO(String message, HttpStatus status, String details, LocalDateTime timestamp) {
        this.message = message;
        this.status = status;
        this.details = details;
        this.timestamp = timestamp;
    }

    // ✅ No-args constructor (needed for serialization/deserialization)
    public ErrorResponseDTO() {}

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public HttpStatus getStatus() { return status; }
    public void setStatus(HttpStatus status) { this.status = status; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}