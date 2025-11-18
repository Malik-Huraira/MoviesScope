package com.moviescope.controller;

import com.moviescope.dto.request.RatingRequest;
import com.moviescope.dto.request.ReviewRequest;
import com.moviescope.dto.response.*;
import com.moviescope.service.MovieService;
import com.moviescope.service.UserMovieService;
import com.moviescope.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final UserMovieService userMovieService;
    private final UserService userService;

    // Existing endpoints
    @GetMapping("/popular")
    public ApiResponse<MovieListResponse> getPopularMovies() {
        List<MovieDTO> movies = movieService.getPopularMovies();
        MovieListResponse movieListResponse = new MovieListResponse(movies);
        return new ApiResponse<>("200", "Success", movieListResponse);
    }

    @GetMapping("/keyword/{keywordId}/movies")
    public ApiResponse<MovieListResponse> getMoviesByKeyword(@PathVariable int keywordId) {
        List<MovieDTO> movies = movieService.getMoviesByKeyword(keywordId);
        MovieListResponse movieListResponse = new MovieListResponse(movies);
        return new ApiResponse<>("200", "Success", movieListResponse);
    }

    @GetMapping("/search")
    public ApiResponse<MovieListResponse> searchMovies(@RequestParam String query) {
        List<MovieDTO> movies = movieService.searchMovies(query);
        MovieListResponse movieListResponse = new MovieListResponse(movies);
        return new ApiResponse<>("200", "Success", movieListResponse);
    }

    @GetMapping("/{movieId}")
    public ApiResponse<MovieDTO> getMovieDetails(@PathVariable int movieId) {
        MovieDTO movie = movieService.getMovieDetails(movieId);
        if (movie == null) {
            return new ApiResponse<>("404", "Movie not found", null);
        }
        return new ApiResponse<>("200", "Success", movie);
    }

    @GetMapping("/analytics")
    public ApiResponse<MovieAnalyticsResponse> getAnalytics() {
        MovieAnalyticsResponse analytics = movieService.getMovieAnalytics();
        return new ApiResponse<>("200", "Success", analytics);
    }

    // NEW ENDPOINTS FOR USER FEATURES

    // Favorite endpoints
    @PostMapping("/{movieId}/favorite")
    public ResponseEntity<ApiResponse<FavoriteResponse>> addToFavorites(
            @PathVariable Integer movieId,
            @RequestParam Long userId) {

        FavoriteResponse response = userMovieService.addToFavorites(userId, movieId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Success", response));
    }

    @DeleteMapping("/{movieId}/favorite")
    public ResponseEntity<ApiResponse<FavoriteResponse>> removeFromFavorites(
            @PathVariable Integer movieId,
            @RequestParam Long userId) {

        FavoriteResponse response = userMovieService.removeFromFavorites(userId, movieId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Success", response));
    }

    @GetMapping("/{movieId}/favorite")
    public ResponseEntity<ApiResponse<Boolean>> isMovieFavorite(
            @PathVariable Integer movieId,
            @RequestParam Long userId) {

        boolean isFavorite = userMovieService.isMovieFavorite(userId, movieId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Success", isFavorite));
    }

    // Rating endpoints
    @PostMapping("/{movieId}/rating")
    public ResponseEntity<ApiResponse<RatingResponse>> addOrUpdateRating(
            @PathVariable Integer movieId,
            @Valid @RequestBody RatingRequest ratingRequest) {

        RatingResponse response = userMovieService.addOrUpdateRating(ratingRequest, movieId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Success", response));
    }

    @GetMapping("/{movieId}/rating")
    public ResponseEntity<ApiResponse<RatingResponse>> getUserRating(
            @PathVariable Integer movieId,
            @RequestParam Long userId) {

        RatingResponse response = userMovieService.getUserRating(userId, movieId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Success", response));
    }

    @GetMapping("/{movieId}/average-rating")
    public ResponseEntity<ApiResponse<Double>> getMovieAverageRating(@PathVariable Integer movieId) {
        Double averageRating = userMovieService.getMovieAverageRating(movieId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Success", averageRating));
    }

    // Review endpoints
    @PostMapping("/{movieId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            @PathVariable Integer movieId,
            @Valid @RequestBody ReviewRequest reviewRequest) {

        ReviewResponse response = userMovieService.addReview(reviewRequest, movieId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Success", response));
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest reviewRequest) {

        ReviewResponse response = userMovieService.updateReview(reviewId, reviewRequest);
        return ResponseEntity.ok(new ApiResponse<>("200", "Success", response));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {

        userMovieService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Review deleted successfully", null));
    }

    @GetMapping("/{movieId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMovieReviews(@PathVariable Integer movieId) {
        List<ReviewResponse> reviews = userMovieService.getMovieReviews(movieId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Success", reviews));
    }

    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getUserReviews(@PathVariable Long userId) {
        List<ReviewResponse> reviews = userMovieService.getUserReviews(userId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Success", reviews));
    }

    // User management endpoints
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
            @RequestParam String username,
            @RequestParam String email) {

        UserDTO user = userService.createUser(username, email);
        return ResponseEntity.ok(new ApiResponse<>("200", "User created successfully", user));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable Long userId) {
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Success", user));
    }
}