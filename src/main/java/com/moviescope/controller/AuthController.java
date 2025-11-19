package com.moviescope.controller;

import com.moviescope.dto.request.LoginRequest;
import com.moviescope.dto.request.RegisterRequest;
import com.moviescope.dto.response.ApiResponse;
import com.moviescope.dto.response.JwtResponse;
import com.moviescope.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(new ApiResponse<>("200", "Login successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<JwtResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        JwtResponse response = authService.registerUser(registerRequest);
        return ResponseEntity.ok(new ApiResponse<>("200", "User registered successfully", response));
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateToken(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.validateToken(token);
        return ResponseEntity.ok(new ApiResponse<>("200", "Token is valid", null));
    }
}