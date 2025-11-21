package com.moviescope.service.impl;

import com.moviescope.dto.request.LoginRequest;
import com.moviescope.dto.request.RegisterRequest;
import com.moviescope.dto.response.JwtResponse;
import com.moviescope.entity.UserEntity;
import com.moviescope.entity.UserRole;
import com.moviescope.repository.UserRepository;
import com.moviescope.service.AuthService;
import com.moviescope.service.EncryptionService;
import com.moviescope.service.UserEncryptionService;
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
    private final UserEncryptionService userEncryptionService;
    private final EncryptionService encryptionService; 

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
    
        // Decrypt user data for response
        user = userEncryptionService.decryptUserForResponse(user);
        String jwt = jwtUtil.generateToken(userDetails, user.getId());
    
        // Generate encrypted user ID
        String encryptedUserId = encryptionService.encryptUserId(user.getId()); 
        return JwtResponse.builder()
                .token(jwt)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .encryptedUserId(encryptedUserId) 
                .build();
    }

    @Override
    @Transactional
    public JwtResponse registerUser(RegisterRequest registerRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        // Check if email exists (using encrypted comparison)
        if (userEncryptionService.isEmailExists(registerRequest.getEmail(), userRepository)) {
            throw new RuntimeException("Email is already in use");
        }

        // Create new user with plain text data
        UserEntity user = UserEntity.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phoneNumber(registerRequest.getPhoneNumber())
                .dateOfBirth(registerRequest.getDateOfBirth())
                .role(registerRequest.getRole() != null ? registerRequest.getRole() : UserRole.ROLE_USER)
                .build();

        // Encrypt sensitive fields before saving
        user = userEncryptionService.encryptUserForSave(user);
        UserEntity savedUser = userRepository.save(user);

        // Decrypt for response
        savedUser = userEncryptionService.decryptUserForResponse(savedUser);

        // Generate token for auto-login
        UserDetails userDetails = savedUser;
        String jwt = jwtUtil.generateToken(userDetails, savedUser.getId());

        // Generate encrypted user ID
        String encryptedUserId = encryptionService.encryptUserId(savedUser.getId()); // ADD THIS LINE

        return JwtResponse.builder()
                .token(jwt)
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .dateOfBirth(savedUser.getDateOfBirth())
                .role(savedUser.getRole())
                .encryptedUserId(encryptedUserId) // ADD THIS LINE
                .build();
    }

    @Override
    public void validateToken(String token) {
        jwtUtil.extractUsername(token);
    }
}