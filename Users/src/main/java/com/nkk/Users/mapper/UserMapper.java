package com.nkk.Users.mapper;

import com.nkk.Users.dto.ResponseDTO;
import com.nkk.Users.entity.Users;
import org.springframework.stereotype.Component;
@Component
public class UserMapper {
    public ResponseDTO mapToUserDTO(Users users) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setUsername(users.getUsername());
        responseDTO.setEmail(users.getEmail());
        return responseDTO;
    }
    public Users mapToUser(ResponseDTO responseDTO) {
        Users users = new Users();
        users.setUsername(responseDTO.getUsername());
        users.setEmail(responseDTO.getEmail());
        users.setPassword(users.getPassword());
        users.setRole(users.getRole());
        users.setCreatedAt(users.getCreatedAt());
        return users;
    }
}
