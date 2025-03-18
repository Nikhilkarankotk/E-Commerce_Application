package com.nkk.Users.dto;

import com.nkk.Users.entity.Role;
import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String email;
    private String password;
}
