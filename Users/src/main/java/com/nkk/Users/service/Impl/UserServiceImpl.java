package com.nkk.Users.service.Impl;

import com.nkk.Users.dto.RegisterDTO;
import com.nkk.Users.dto.ResponseDTO;
import com.nkk.Users.dto.UserDTO;
import com.nkk.Users.entity.Users;
import com.nkk.Users.mapper.UserMapper;
import com.nkk.Users.repository.UserRepository;
import com.nkk.Users.service.IUserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Transactional
    public UserDTO registerUser(RegisterDTO registerDTO) {
        // Validate email uniqueness
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        // Create a new user
        Users user = userMapper.mapToUser(registerDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash the password
        user.setCreatedAt(LocalDateTime.now());
        // Save the user
        Users savedUser = userRepository.save(user);
        // Map to DTO and return
        return userMapper.mapToUserDTO(savedUser);
    }


    public UserDTO getUserById(Long userId) {
        Users users = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return userMapper.mapToUserDTO(users);
    }

}
