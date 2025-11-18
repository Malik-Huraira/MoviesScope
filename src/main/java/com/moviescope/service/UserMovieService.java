package com.moviescope.service;

import com.moviescope.dto.request.RatingRequest;
import com.moviescope.dto.request.ReviewRequest;
import com.moviescope.dto.response.FavoriteResponse;
import com.moviescope.dto.response.RatingResponse;
import com.moviescope.dto.response.ReviewResponse;
import java.util.List;

public interface UserMovieService {

    // Favorite methods
    FavoriteResponse addToFavorites(Long userId, Integer movieId);

    FavoriteResponse removeFromFavorites(Long userId, Integer movieId);

    boolean isMovieFavorite(Long userId, Integer movieId);

    List<Integer> getUserFavoriteMovieIds(Long userId);

    // Rating methods
    RatingResponse addOrUpdateRating(RatingRequest ratingRequest, Integer movieId);

    RatingResponse getUserRating(Long userId, Integer movieId);

    Double getMovieAverageRating(Integer movieId);

    // Review methods
    ReviewResponse addReview(ReviewRequest reviewRequest, Integer movieId);

    ReviewResponse updateReview(Long reviewId, ReviewRequest reviewRequest);

    void deleteReview(Long reviewId, Long userId);

    List<ReviewResponse> getMovieReviews(Integer movieId);

    List<ReviewResponse> getUserReviews(Long userId);
}