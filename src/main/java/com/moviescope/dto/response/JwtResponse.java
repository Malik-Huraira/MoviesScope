package com.moviescope.dto.response;

import com.moviescope.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private UserRole role;
}