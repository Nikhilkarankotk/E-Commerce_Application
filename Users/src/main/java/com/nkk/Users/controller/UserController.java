package com.nkk.Users.controller;

import com.nkk.Users.dto.RegisterDTO;
import com.nkk.Users.dto.ResponseDTO;
import com.nkk.Users.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserService userService;
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody RegisterDTO registerDTO) {
        ResponseDTO registeredUser = userService.registerUser(registerDTO);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO> getUserById(@PathVariable Long userId) {
        ResponseDTO responseDTO = userService.getUserById(userId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
