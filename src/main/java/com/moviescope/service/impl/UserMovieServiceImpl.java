package com.moviescope.service.impl;

import com.moviescope.dto.request.RatingRequest;
import com.moviescope.dto.request.ReviewRequest;
import com.moviescope.dto.response.FavoriteResponse;
import com.moviescope.dto.response.RatingResponse;
import com.moviescope.dto.response.ReviewResponse;
import com.moviescope.entity.*;
import com.moviescope.repository.*;
import com.moviescope.service.MovieService;
import com.moviescope.service.UserMovieService;
import com.moviescope.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserMovieServiceImpl implements UserMovieService {

    private final UserFavoriteRepository userFavoriteRepository;
    private final UserRatingRepository userRatingRepository;
    private final MovieReviewRepository movieReviewRepository;
    private final UserRepository userRepository;
    private final MovieService movieService;
    private final UserService userService;

    // Favorite Methods

    @Override
    @Transactional
    public FavoriteResponse addToFavorites(Long userId, Integer movieId) {
        // Validate user and movie
        userService.getUserEntity(userId);
        movieService.fetchMovieEntity(movieId); // This will fetch from TMDB if not exists

        // Check if already favorited
        if (userFavoriteRepository.existsByUserIdAndMovieId(userId, movieId)) {
            return FavoriteResponse.builder()
                    .movieId(movieId)
                    .favorite(true)
                    .message("Movie already in favorites")
                    .build();
        }

        UserFavorite favorite = UserFavorite.builder()
                .user(userRepository.getReferenceById(userId))
                .movie(movieService.fetchMovieEntity(movieId))
                .build();

        userFavoriteRepository.save(favorite);

        return FavoriteResponse.builder()
                .movieId(movieId)
                .favorite(true)
                .message("Movie added to favorites")
                .build();
    }

    @Override
    @Transactional
    public FavoriteResponse removeFromFavorites(Long userId, Integer movieId) {
        userFavoriteRepository.deleteByUserIdAndMovieId(userId, movieId);

        return FavoriteResponse.builder()
                .movieId(movieId)
                .favorite(false)
                .message("Movie removed from favorites")
                .build();
    }

    @Override
    public boolean isMovieFavorite(Long userId, Integer movieId) {
        return userFavoriteRepository.existsByUserIdAndMovieId(userId, movieId);
    }

    @Override
    public List<Integer> getUserFavoriteMovieIds(Long userId) {
        return userFavoriteRepository.findByUserId(userId).stream()
                .map(favorite -> favorite.getMovie().getId())
                .collect(Collectors.toList());
    }

    // Rating Methods

    @Override
    @Transactional
    public RatingResponse addOrUpdateRating(RatingRequest ratingRequest, Integer movieId) {
        Long userId = ratingRequest.getUserId();
        Double rating = ratingRequest.getRating();

        // Validate user and movie
        userService.getUserEntity(userId);
        movieService.fetchMovieEntity(movieId);

        // Check if rating exists
        UserRating userRating = userRatingRepository.findByUserIdAndMovieId(userId, movieId)
                .orElse(null);

        if (userRating != null) {
            // Update existing rating
            userRating.setRating(rating);
            userRating.setRatedAt(LocalDateTime.now());
        } else {
            // Create new rating
            userRating = UserRating.builder()
                    .user(userRepository.getReferenceById(userId))
                    .movie(movieService.fetchMovieEntity(movieId))
                    .rating(rating)
                    .build();
        }

        UserRating savedRating = userRatingRepository.save(userRating);

        return RatingResponse.builder()
                .movieId(movieId)
                .rating(savedRating.getRating())
                .ratedAt(savedRating.getRatedAt())
                .message(userRating.getId() != null ? "Rating updated" : "Rating added")
                .build();
    }

    @Override
    public RatingResponse getUserRating(Long userId, Integer movieId) {
        UserRating userRating = userRatingRepository.findByUserIdAndMovieId(userId, movieId)
                .orElse(null);

        if (userRating == null) {
            return RatingResponse.builder()
                    .movieId(movieId)
                    .rating(0.0)
                    .message("No rating found")
                    .build();
        }

        return RatingResponse.builder()
                .movieId(movieId)
                .rating(userRating.getRating())
                .ratedAt(userRating.getRatedAt())
                .message("Rating found")
                .build();
    }

    @Override
    public Double getMovieAverageRating(Integer movieId) {
        Double average = userRatingRepository.findAverageRatingByMovieId(movieId);
        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0;
    }

    // Review Methods

    @Override
    @Transactional
    public ReviewResponse addReview(ReviewRequest reviewRequest, Integer movieId) {
        Long userId = reviewRequest.getUserId();

        // Validate user and movie
        UserEntity user = userService.getUserEntity(userId);
        movieService.fetchMovieEntity(movieId);

        // Check if user already reviewed this movie
        if (movieReviewRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new RuntimeException("You have already reviewed this movie");
        }

        MovieReview review = MovieReview.builder()
                .user(user)
                .movie(movieService.fetchMovieEntity(movieId))
                .reviewText(reviewRequest.getReviewText())
                .build();

        MovieReview savedReview = movieReviewRepository.save(review);

        return ReviewResponse.builder()
                .reviewId(savedReview.getId())
                .movieId(movieId)
                .userId(userId)
                .username(user.getUsername())
                .reviewText(savedReview.getReviewText())
                .createdAt(savedReview.getCreatedAt())
                .updatedAt(savedReview.getUpdatedAt())
                .message("Review added successfully")
                .build();
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest reviewRequest) {
        MovieReview review = movieReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Check if user owns the review
        if (!review.getUser().getId().equals(reviewRequest.getUserId())) {
            throw new RuntimeException("You can only update your own reviews");
        }

        review.setReviewText(reviewRequest.getReviewText());
        MovieReview updatedReview = movieReviewRepository.save(review);

        return ReviewResponse.builder()
                .reviewId(updatedReview.getId())
                .movieId(updatedReview.getMovie().getId())
                .userId(updatedReview.getUser().getId())
                .username(updatedReview.getUser().getUsername())
                .reviewText(updatedReview.getReviewText())
                .createdAt(updatedReview.getCreatedAt())
                .updatedAt(updatedReview.getUpdatedAt())
                .message("Review updated successfully")
                .build();
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        MovieReview review = movieReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }

        movieReviewRepository.delete(review);
    }

    @Override
    public List<ReviewResponse> getMovieReviews(Integer movieId) {
        return movieReviewRepository.findReviewsByMovieIdWithUser(movieId).stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getUserReviews(Long userId) {
        return movieReviewRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse toReviewResponse(MovieReview review) {
        return ReviewResponse.builder()
                .reviewId(review.getId())
                .movieId(review.getMovie().getId())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .reviewText(review.getReviewText())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}