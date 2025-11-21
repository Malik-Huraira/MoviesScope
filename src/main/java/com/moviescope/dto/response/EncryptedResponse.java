package com.moviescope.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedResponse<T> {
    private String responseCode;
    private String responseDesc;
    private String encryptedData; // Base64 encoded encrypted JSON
    private String encryptionKey; // Key identifier (for key rotation)
}