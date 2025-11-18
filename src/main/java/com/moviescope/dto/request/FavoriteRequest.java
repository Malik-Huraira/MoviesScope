package com.moviescope.dto.request;

import lombok.Data;

@Data
public class FavoriteRequest {
    private Long userId;
    private boolean favorite;
}