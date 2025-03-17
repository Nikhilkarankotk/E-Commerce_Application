package com.nkk.Users.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}
