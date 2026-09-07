package com.wali.auth_service.controller;

import com.wali.auth_service.Request.AuthRegisterRequest;
import com.wali.auth_service.Request.AuthLoginRequest;
import com.wali.auth_service.entities.UserEntity;
import com.wali.auth_service.exceptions.UserAlreadyExistsException;
import com.wali.auth_service.response.AuthResponse;
import com.wali.auth_service.security.jwt.JwtTokenUtil;
import com.wali.auth_service.services.user.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenUtil jwtUtil;
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthLoginRequest request) {
        log.info("Login attempt for email={}", request.getEmail());

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword())
            );

            UserEntity user = (UserEntity) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);


            AuthResponse response = AuthResponse.builder()
                    .accessToken(accessToken)
                    .data(Map.of("id", user.getId()))
                    .message("Connection successful")
                    .build();

            log.info("Login successful for email={}", request.getEmail());

            return ResponseEntity.ok().body(response);
        } catch (BadCredentialsException ex) {
            log.warn("Login failed: invalid credentials for email={}",
                    request.getEmail());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthResponse.builder()
                            .message("Incorrect password or email")
                    .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthRegisterRequest request) {
        log.info("Registration attempt for email={}", request.getEmail());

        try {
          UserEntity user =  userService.createUser(request);

            String accessToken = jwtUtil.generateAccessToken(user);
            AuthResponse response = AuthResponse.builder()
                    .data(Map.of("id", user.getId()))
                    .message("Register successful")
                    .accessToken(accessToken)
                    .build();

            log.info("Registration successful for email={}", request.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (UserAlreadyExistsException ex) {
            log.warn("Registration failed: email already exists, email={}", request.getEmail());

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(
                                AuthResponse.builder().message("Email already exists").build()
                        );

        } catch (Exception ex) {
            log.error("Unexpected error during registration for email={}", request.getEmail(), ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            AuthResponse.builder().message("Internal Error").build()
                    );
        }
    }
}
