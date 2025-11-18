package com.moviescope.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long reviewId;
    private Integer movieId;
    private Long userId;
    private String username;
    private String reviewText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
}