package com.moviescope.service;

import com.moviescope.utils.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EncryptionService {

    private final EncryptionUtil encryptionUtil;

    public String encryptSensitiveData(String data) {
        return encryptionUtil.encryptField(data);
    }

    public String decryptSensitiveData(String encryptedData) {
        return encryptionUtil.decryptField(encryptedData);
    }

    // Encrypt user profile for API responses
    public String encryptUserId(Long userId) {
        return encryptionUtil.encrypt(userId.toString());
    }

    public Long decryptUserId(String encryptedUserId) {
        return Long.parseLong(encryptionUtil.decrypt(encryptedUserId));
    }
}