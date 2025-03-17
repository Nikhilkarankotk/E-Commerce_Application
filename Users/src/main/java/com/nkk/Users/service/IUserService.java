package com.nkk.Users.service;

import com.nkk.Users.dto.RegisterDTO;
import com.nkk.Users.dto.ResponseDTO;
import com.nkk.Users.dto.UserDTO;

public interface IUserService {
    /**
     *
     * @param registerDTO
     * @return
     */
    UserDTO registerUser(RegisterDTO registerDTO);

    /**
     *
     * @param userId
     * @return
     */
    UserDTO getUserById(Long userId);
}
