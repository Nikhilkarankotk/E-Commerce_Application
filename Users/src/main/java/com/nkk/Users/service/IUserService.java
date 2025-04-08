package com.nkk.Users.service;

import com.nkk.Users.dto.RegisterDTO;
import com.nkk.Users.dto.ResponseDTO;
import com.nkk.Users.dto.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserService {
    /**
     *
     * @param registerDTO
     * @return
     */
    UserDTO registerUser(RegisterDTO registerDTO);

    /**
     *
     * @param registerDTO
     * @return
     */
    UserDTO registerAdmin(RegisterDTO registerDTO);

    /**
     *
     * @param userId
     * @return
     */
    UserDTO getUserById(Long userId);

    /**
     *
     * @param email
     * @return
     */
    UserDTO getUserByEmail(String email);

    /**
     *
     * @return
     */
    List<UserDTO> getAllUsers();

    /**
     *
     * @param userId
     * @param registerDTO
     * @return
     */
    UserDTO updateUser(Long userId, RegisterDTO registerDTO);

    /*

     */
    Long getUserIdByEmail(String email);
    /**
     *
     * @param email
     */
    void initiatePasswordReset(String email);

    /**
     *
     * @param token
     * @param newPassword
     */
    void updatePasswordWithToken(String token, String newPassword);

    /**
     *
     * @param token
     * @param newPassword
     */
    void updatePassword(String token, String currentPassword, String newPassword);

    /**
     *
     * @param id
     */
    void deleteUser(Long id);

}
