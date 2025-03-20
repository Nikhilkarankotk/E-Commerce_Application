package com.nkk.Users.service.RefreshToken;

import com.nkk.Users.config.Jwt.JwtUtil;
import com.nkk.Users.entity.RefreshToken;
import com.nkk.Users.entity.Users;
import com.nkk.Users.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private  RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByUser(Users user) {
        return refreshTokenRepository.findByUser(user);
    }

    public RefreshToken generateRefreshToken(Users user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshToken.setUser(user);
        return refreshTokenRepository.save(refreshToken);
    }
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }
        return refreshToken;

    }
    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
