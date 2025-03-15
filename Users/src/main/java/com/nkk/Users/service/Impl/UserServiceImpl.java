package com.nkk.Users.service.Impl;

import com.nkk.Users.dto.RegisterDTO;
import com.nkk.Users.dto.ResponseDTO;
import com.nkk.Users.entity.Users;
import com.nkk.Users.mapper.UserMapper;
import com.nkk.Users.repository.UserRepository;
import com.nkk.Users.service.IUserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Transactional
    public ResponseDTO registerUser(RegisterDTO registerDTO) {
        // Validate email uniqueness
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        // Create a new user
        Users user = new Users();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword()); // Hash the password
        user.setRole("USER"); // Default role
        user.setCreatedAt(LocalDateTime.now());
        // Save the user
        Users savedUser = userRepository.save(user);
        // Map to DTO and return
        return userMapper.mapToUserDTO(savedUser);
    }


    public ResponseDTO getUserById(Long userId) {
        Users users = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return userMapper.mapToUserDTO(users);
    }

}
