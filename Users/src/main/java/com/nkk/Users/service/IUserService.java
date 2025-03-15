package com.nkk.Users.service;

import com.nkk.Users.dto.RegisterDTO;
import com.nkk.Users.dto.ResponseDTO;

public interface IUserService {
    /**
     *
     * @param registerDTO
     * @return
     */
    ResponseDTO registerUser(RegisterDTO registerDTO);

    /**
     *
     * @param userId
     * @return
     */
    ResponseDTO getUserById(Long userId);
}
