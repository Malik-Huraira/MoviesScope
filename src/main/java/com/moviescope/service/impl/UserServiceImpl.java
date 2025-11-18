package com.moviescope.service.impl;

import com.moviescope.dto.response.UserDTO;
import com.moviescope.entity.UserEntity;
import com.moviescope.repository.UserRepository;
import com.moviescope.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDTO createUser(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        UserEntity user = UserEntity.builder()
                .username(username)
                .email(email)
                .build();

        UserEntity savedUser = userRepository.save(user);
        return toDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(Long userId) {
        UserEntity user = getUserEntity(userId);
        return toDTO(user);
    }

    @Override
    public UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Override
    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    private UserDTO toDTO(UserEntity user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}