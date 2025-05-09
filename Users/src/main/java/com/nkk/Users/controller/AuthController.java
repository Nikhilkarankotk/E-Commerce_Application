package com.nkk.Users.controller;

import com.nkk.Users.config.CustomUserDetailsService;
import com.nkk.Users.config.Jwt.JwtUtil;
import com.nkk.Users.config.TokenBlacklist;
import com.nkk.Users.dto.LoginRequest;
import com.nkk.Users.dto.LoginResponse;
import com.nkk.Users.entity.RefreshToken;
import com.nkk.Users.entity.Users;
import com.nkk.Users.exception.ResourceNotFoundException;
import com.nkk.Users.repository.UserRepository;
import com.nkk.Users.service.IUserService;
import com.nkk.Users.service.RefreshToken.RefreshTokenService;
import com.nkk.Users.service.auditing.AuditLogService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IUserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private TokenBlacklist tokenBlacklist;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AuditLogService auditLogService;


@PostMapping("/login")
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    // Authenticate the user
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), loginRequest.getPassword()
            ));
    // Load user details
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    Users user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

    // Check if the user already has an active refresh token
    RefreshToken existingRefreshToken = refreshTokenService.findByUser(user);
    if (existingRefreshToken != null) {
        // Delete the existing refresh token
        refreshTokenService.deleteRefreshToken(existingRefreshToken.getToken());
    }
    // Generate a new refresh token
    RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user);
    // Log the login
    auditLogService.logAction("LOGIN", user.getEmail(), "User logged in successfully");
    // Set the new refresh token in the HTTP-only cookie
    setRefreshTokenCookie(response, refreshToken.getToken());
    // Return the access token in the response body
    return ResponseEntity.ok(new LoginResponse(JwtUtil.generateToken(userDetails)));
}

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Refresh token not found"));
        }
        try {
            // Verify the refresh token
            RefreshToken verifiedRefreshToken = refreshTokenService.verifyRefreshToken(refreshToken);
            Users user = verifiedRefreshToken.getUser();
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
            // Generate new access token
            String accessToken = jwtUtil.generateToken(userDetails);

            // Rotate refresh token
            refreshTokenService.deleteRefreshToken(refreshToken);
            RefreshToken newRefreshToken = refreshTokenService.generateRefreshToken(user);
            // Set the new refresh token in the HTTP-only cookie
            setRefreshTokenCookie(response, newRefreshToken.getToken());
            return ResponseEntity.ok(new LoginResponse(accessToken));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid refresh token"));
        }
    }
    //Helper method to set refresh token in a cookie
    private void setRefreshTokenCookie(HttpServletResponse response, String token){
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Use HTTPS in production
        cookie.setPath("/auth/refresh-token");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(cookie);
    }
    // Logout
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        tokenBlacklist.blacklistToken(jwtToken); // Remove "Bearer " prefix
        // Log the logout
        String username = jwtUtil.extractUsername(jwtToken);
        auditLogService.logAction("LOGOUT", username, "User logged out successfully");
        return ResponseEntity.ok().build();
    }
}
