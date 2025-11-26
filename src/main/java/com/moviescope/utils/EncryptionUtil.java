package com.moviescope.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Component
public class EncryptionUtil {

    @Value("${encryption.secret.key}")
    private String secretKey;

    private static final String ALGORITHM = "AES";

    @PostConstruct
    public void init() {
        System.out.println("Actual Secret Key Value = " + secretKey);
        System.out.println("=== ENCRYPTION UTIL INITIALIZED ===");
        System.out.println("Secret Key: " + (secretKey != null ? "***LOADED***" : "NULL"));
        System.out.println("Key Length: " + (secretKey != null ? secretKey.length() : "N/A"));
        System.out.println("===================================");

        if (secretKey == null) {
            throw new IllegalStateException("❌ ENCRYPTION KEY IS NULL! Check application.properties");
        }
    }

    private SecretKeySpec getSecretKey() {
        try {
            // Use SHA-256 to hash the key and then take first 16 bytes for AES-128
            byte[] key = secretKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // Use first 16 bytes for AES-128

            return new SecretKeySpec(key, ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to create secret key from: '" + secretKey + "'. Error: " + e.getMessage(), e);
        }
    }

    public String encrypt(String data) {
        if (data == null)
            return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed for data: '" + data + "'. Error: " + e.getMessage(), e);
        }
    }

    public String decrypt(String encryptedData) {
        if (encryptedData == null)
            return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed for data: '" + encryptedData + "'. Error: " + e.getMessage(),
                    e);
        }
    }

    public String encryptField(String field) {
        if (field == null || field.trim().isEmpty()) {
            return field;
        }
        try {
            return "enc:" + encrypt(field);
        } catch (Exception e) {
            throw new RuntimeException("Field encryption failed for: '" + field + "'. Error: " + e.getMessage(), e);
        }
    }

    public String decryptField(String encryptedField) {
        if (encryptedField == null || !encryptedField.startsWith("enc:")) {
            return encryptedField;
        }
        try {
            return decrypt(encryptedField.substring(4));
        } catch (Exception e) {
            throw new RuntimeException(
                    "Field decryption failed for: '" + encryptedField + "'. Error: " + e.getMessage(), e);
        }
    }
}