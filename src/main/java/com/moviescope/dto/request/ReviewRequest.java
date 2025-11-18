package com.moviescope.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ReviewRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Review text cannot be empty")
    private String reviewText;
}