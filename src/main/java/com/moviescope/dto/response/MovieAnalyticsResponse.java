package com.moviescope.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieAnalyticsResponse {
    private int totalMovies;
    private double averageRating;
    private Map<String, Long> moviesPerGenre;
    private long totalFavorites;
    private long totalReviews;
    private long totalRatings;
}