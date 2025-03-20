package com.nkk.Users.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String refreshToken;
    public LoginResponse(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
