package com.nkk.Users.repository;

import com.nkk.Users.entity.RefreshToken;
import com.nkk.Users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    RefreshToken findByUser(Users user);

}
