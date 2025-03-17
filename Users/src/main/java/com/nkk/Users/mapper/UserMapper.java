package com.nkk.Users.mapper;

import com.nkk.Users.dto.RegisterDTO;
import com.nkk.Users.dto.ResponseDTO;
import com.nkk.Users.dto.UserDTO;
import com.nkk.Users.entity.Users;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public UserDTO mapToUserDTO(Users users) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(users.getUserId());
        userDTO.setUsername(users.getUsername());
        userDTO.setEmail(users.getEmail());
        userDTO.setRole(users.getRole());
        userDTO.setCreatedAt(users.getCreatedAt());
        return userDTO;
    }
    public Users mapToUser(RegisterDTO registerDTO) {
        Users users = new Users();
        users.setUsername(registerDTO.getUsername());
        users.setEmail(registerDTO.getEmail());
        users.setPassword(registerDTO.getPassword());
        users.setRole("USER");
        users.setCreatedAt(LocalDateTime.now());
        return users;
    }
}
