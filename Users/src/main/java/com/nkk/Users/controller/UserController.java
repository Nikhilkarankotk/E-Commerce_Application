package com.nkk.Users.controller;

import com.nkk.Users.config.Jwt.JwtUtil;
import com.nkk.Users.dto.LoginRequest;
import com.nkk.Users.dto.RegisterDTO;
import com.nkk.Users.dto.UserDTO;
import com.nkk.Users.entity.Users;
import com.nkk.Users.service.IUserService;
import com.nkk.Users.config.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    // User endpoints
    // Register user (exclude email verification)
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterDTO registerDTO) {
        UserDTO userDTO = userService.registerUser(registerDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }
    //update profile
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody RegisterDTO register) {
        UserDTO updatedUser = userService.updateUser(id,register);
        return ResponseEntity.ok(updatedUser);
    }
    // Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam String email) {
        userService.initiatePasswordReset(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-password-with-token")
    public ResponseEntity<Void> updatePasswordWithToken(
            @RequestParam String token,
            @RequestParam String newPassword) {
        userService.updatePasswordWithToken(token, newPassword);
        return ResponseEntity.ok().build();
    }
    // Update Password
    @PostMapping("/update-password")
    public ResponseEntity<Void> updatePassword(
            @RequestHeader("Authorization") String authToken,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        String token= authToken.substring(7).trim(); // Extract and trim the token
        System.out.println("Received update-password request");
        userService.updatePassword(token, currentPassword,newPassword);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/email")
    public ResponseEntity<Long> getUserIdByEmail(@RequestParam String email) {
        Long userId = userService.getUserIdByEmail(email);
        if(userId != null) {
            return ResponseEntity.ok(userId);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserById(userId);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    // User and Admin endpoints
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractUsername(token.substring(7));
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    // Admin-only endpoints
    @PostMapping("/admin/register")
    public ResponseEntity<UserDTO> registerAdmin(@RequestBody RegisterDTO registerDTO) {
        UserDTO admin = userService.registerAdmin(registerDTO);
        return ResponseEntity.ok(admin);
    }
    @GetMapping("/admin/all-users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
