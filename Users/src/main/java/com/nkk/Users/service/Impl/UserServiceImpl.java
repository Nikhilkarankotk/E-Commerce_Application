package com.nkk.Users.service.Impl;

import com.nkk.Users.config.CustomUserDetailsService;
import com.nkk.Users.config.Jwt.JwtUtil;
import com.nkk.Users.dto.RegisterDTO;
import com.nkk.Users.dto.UserDTO;
import com.nkk.Users.entity.Users;
import com.nkk.Users.mapper.UserMapper;
import com.nkk.Users.repository.UserRepository;
import com.nkk.Users.service.IUserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
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
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Transactional
    public UserDTO registerUser(RegisterDTO registerDTO) {
       validatePassword(registerDTO.getPassword());
        // Validate email uniqueness
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        // Create a new user
        Users user = userMapper.mapToUser(registerDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Hash the password
        user.setCreatedAt(LocalDateTime.now());
        // Save the user
        Users savedUser = userRepository.save(user);
        // Map to DTO and return
        return userMapper.mapToUserDTO(savedUser);
    }

    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*\\d.*")) {
            throw new RuntimeException("Password must contain at least one digit");
        }
    }
    public void initiatePasswordReset(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        String resetToken = jwtUtil.generateResetToken(email);
        // For now, just log the reset token (we'll integrate email service later)
        System.out.println("Reset Token: " + resetToken);
    }
    @Transactional
    public void updatePasswordWithToken(String token, String newPassword) {
        // Step 1: Extract the email from the token
        String email = jwtUtil.extractUsername(token);
        // Step 2: Fetch the user by email
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        // Step 3: Validate the new password
        validatePassword(newPassword);
        // Step 4: Update the password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    @Transactional
    public void updatePassword(String token, String currentPassword, String newPassword) {
        String email = jwtUtil.extractUsername(token);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        // Step 3: Validate the current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        validatePassword(newPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        System.out.println("Password updated successfully"); // Log if password is updated
    }


    public UserDTO getUserById(Long userId) {
        Users users = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return userMapper.mapToUserDTO(users);
    }

    @Transactional
    @Override
    public UserDTO updateUser(Long userId, RegisterDTO registerDTO) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        Users updatedUser = userRepository.save(user);
        return userMapper.mapToUserDTO(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }

}
