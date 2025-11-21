package com.moviescope.service;

import com.moviescope.entity.UserEntity;
import com.moviescope.repository.UserRepository;
import com.moviescope.utils.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserEncryptionService {

    private final EncryptionUtil encryptionUtil;

    @Transactional
    public UserEntity encryptUserForSave(UserEntity user) {
        if (user.getEmail() != null && !user.getEmail().startsWith("enc:")) {
            user.setEmail(encryptionUtil.encryptField(user.getEmail()));
        }
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().startsWith("enc:")) {
            user.setPhoneNumber(encryptionUtil.encryptField(user.getPhoneNumber()));
        }
        if (user.getDateOfBirth() != null && !user.getDateOfBirth().startsWith("enc:")) {
            user.setDateOfBirth(encryptionUtil.encryptField(user.getDateOfBirth()));
        }
        return user;
    }

    public UserEntity decryptUserForResponse(UserEntity user) {
        if (user.getEmail() != null && user.getEmail().startsWith("enc:")) {
            user.setEmail(encryptionUtil.decryptField(user.getEmail()));
        }
        if (user.getPhoneNumber() != null && user.getPhoneNumber().startsWith("enc:")) {
            user.setPhoneNumber(encryptionUtil.decryptField(user.getPhoneNumber()));
        }
        if (user.getDateOfBirth() != null && user.getDateOfBirth().startsWith("enc:")) {
            user.setDateOfBirth(encryptionUtil.decryptField(user.getDateOfBirth()));
        }
        return user;
    }

    // Helper method to check if email exists (with encryption)
    public String getEncryptedEmail(String email) {
        return encryptionUtil.encryptField(email);
    }

    // Helper method to check if user exists by encrypted email
    public boolean isEmailExists(String email, UserRepository userRepository) {
        String encryptedEmail = getEncryptedEmail(email);
        return userRepository.existsByEmail(encryptedEmail);
    }
}