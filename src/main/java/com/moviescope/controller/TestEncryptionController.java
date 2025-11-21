package com.moviescope.controller;

import com.moviescope.service.EncryptionService;
import com.moviescope.utils.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestEncryptionController {

    private final EncryptionUtil encryptionUtil;
    private final EncryptionService encryptionService;

    @GetMapping("/encrypt/{data}")
    public String testEncryption(@PathVariable String data) {
        String encrypted = encryptionUtil.encrypt(data);
        String decrypted = encryptionUtil.decrypt(encrypted);

        return String.format("Original: %s | Encrypted: %s | Decrypted: %s",
                data, encrypted, decrypted);
    }

    @GetMapping("/encrypt-field/{field}")
    public String testFieldEncryption(@PathVariable String field) {
        String encrypted = encryptionUtil.encryptField(field);
        String decrypted = encryptionUtil.decryptField(encrypted);

        return String.format("Original: %s | Encrypted Field: %s | Decrypted: %s",
                field, encrypted, decrypted);
    }
}