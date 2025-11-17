package com.moviescope.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.moviescope.dto.response.ApiResponse;
import com.moviescope.dto.response.MovieDTO;
import com.moviescope.dto.response.MovieListResponse;
import com.moviescope.service.MovieService;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

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

    // Fixed: Proper generic type
    @GetMapping("/search")
    public ApiResponse<MovieListResponse> searchMovies(@RequestParam String query) {
        List<MovieDTO> movies = movieService.searchMovies(query);
        MovieListResponse movieListResponse = new MovieListResponse(movies);
        return new ApiResponse<>("200", "Success", movieListResponse);
    }

    // Fixed: Proper generic type
    @GetMapping("/{movieId}")
    public ApiResponse<MovieDTO> getMovieDetails(@PathVariable int movieId) {
        MovieDTO movie = movieService.getMovieDetails(movieId);
        if (movie == null) {
            return new ApiResponse<>("404", "Movie not found", null);
        }
        return new ApiResponse<>("200", "Success", movie);
    }

    // Fixed: Proper generic type for Map
    @GetMapping("/analytics")
    public ApiResponse<Map<String, Object>> getAnalytics() {
        Map<String, Object> analytics = movieService.getMovieAnalytics();
        return new ApiResponse<>("200", "Success", analytics);
    }
}