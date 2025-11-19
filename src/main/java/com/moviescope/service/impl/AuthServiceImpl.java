package com.moviescope.service.impl;

import com.moviescope.dto.request.LoginRequest;
import com.moviescope.dto.request.RegisterRequest;
import com.moviescope.dto.response.JwtResponse;
import com.moviescope.entity.UserEntity;
import com.moviescope.entity.UserRole;
import com.moviescope.repository.UserRepository;
import com.moviescope.service.AuthService;
import com.moviescope.utils.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwt = jwtUtil.generateToken(userDetails, user.getId());

        return JwtResponse.builder()
                .token(jwt)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    @Transactional
    public JwtResponse registerUser(RegisterRequest registerRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        // Check if email exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create new user
        UserEntity user = UserEntity.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole() != null ? registerRequest.getRole() : UserRole.ROLE_USER)
                .build();

        UserEntity savedUser = userRepository.save(user);

        // Generate token for auto-login after registration
        UserDetails userDetails = savedUser;
        String jwt = jwtUtil.generateToken(userDetails, savedUser.getId());

        return JwtResponse.builder()
                .token(jwt)
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    @Override
    public void validateToken(String token) {
        jwtUtil.extractUsername(token);
    }
}