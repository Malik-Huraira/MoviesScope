package com.moviescope.service;

import com.moviescope.dto.request.LoginRequest;
import com.moviescope.dto.request.RegisterRequest;
import com.moviescope.dto.response.JwtResponse;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);

    JwtResponse registerUser(RegisterRequest registerRequest);

    void validateToken(String token);
}