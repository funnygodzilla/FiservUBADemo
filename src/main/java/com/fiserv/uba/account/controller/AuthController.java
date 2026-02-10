package com.fiserv.uba.account.controller;

import com.fiserv.uba.account.dto.*;
import com.fiserv.uba.account.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/auth/login - User login
     * Authenticates user and returns JWT token with claims
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request received for user: {}", loginRequest.getUsername());

        LoginResponse loginResponse = authService.login(loginRequest);

        ApiResponse<LoginResponse> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Login successful",
                loginResponse
        );

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/register - User registration
     * Creates a new user account
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration request received for user: {}", registerRequest.getUsername());

        UserDTO userDTO = authService.register(registerRequest);

        ApiResponse<UserDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "User registered successfully",
                userDTO
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/auth/me - Get current user
     * Returns authenticated user details
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        log.info("Fetching current user details");

        UserDTO userDTO = authService.getCurrentUser();

        ApiResponse<UserDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "User details retrieved successfully",
                userDTO
        );

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/logout - User logout
     * (Client-side token invalidation recommended)
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> logout() {
        log.info("Logout request received");

        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Logout successful. Please remove the token from client.",
                null
        );

        return ResponseEntity.ok(response);
    }
}

