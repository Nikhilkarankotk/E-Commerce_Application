package com.nkk.Users.mapper;

import com.nkk.Users.dto.RegisterDTO;
import com.nkk.Users.dto.UserDTO;
import com.nkk.Users.entity.Users;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public UserDTO mapToUserDTO(Users users) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(users.getEmail());
        userDTO.setRole(users.getRole().name());
        return userDTO;
    }
    public Users mapToUser(RegisterDTO registerDTO) {
        Users users = new Users();
        users.setUsername(registerDTO.getUsername());
        users.setEmail(registerDTO.getEmail());
        users.setPassword(registerDTO.getPassword());
        users.setCreatedAt(LocalDateTime.now());
        return users;
    }
}
