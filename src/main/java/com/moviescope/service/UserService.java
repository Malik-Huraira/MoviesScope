package com.moviescope.service;

import com.moviescope.dto.response.UserDTO;
import com.moviescope.entity.UserEntity;

public interface UserService {
    UserDTO createUser(String username, String email, String phoneNumber, String dateOfBirth);

    UserDTO getUserById(Long userId);

    UserEntity getUserEntity(Long userId);

    boolean userExists(Long userId);
}