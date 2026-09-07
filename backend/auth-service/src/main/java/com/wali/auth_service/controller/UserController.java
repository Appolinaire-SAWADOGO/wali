package com.wali.auth_service.controller;

import com.wali.auth_service.dto.response.UserResponseDto;
import com.wali.auth_service.exceptions.UserNotFoundException;
import com.wali.auth_service.response.AuthResponse;
import com.wali.auth_service.services.user.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    private ResponseEntity<AuthResponse> getUser(@PathVariable String id) {
        log.info("Getting user for userId={}", id);

        try {
            UserResponseDto userResponseDto = userService.getUserById(id);

            log.info("User found successfully for userId={}", id);

            return ResponseEntity.status(HttpStatus.OK).body(AuthResponse.builder()
                            .data(userResponseDto)
                    .build());
        }catch (UserNotFoundException e) {
            log.warn("User not found for userId={}", id);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    AuthResponse.builder()
                            .message("User not found")
                            .build());
        }

    }
}
