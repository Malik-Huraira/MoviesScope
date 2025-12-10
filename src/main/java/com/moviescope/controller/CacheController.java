package com.moviescope.controller;

import com.moviescope.dto.response.ApiResponse;
import com.moviescope.service.impl.MovieServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private MovieServiceImpl movieService;
    
    @PostMapping("/clear")
    public ApiResponse<String> clearAllCaches() {
        movieService.clearAllCaches();
        return new ApiResponse<>("200", "All caches cleared successfully", null);
    }
    
    @PostMapping("/refresh/{movieId}")
    public ApiResponse<String> refreshMovieCache(@PathVariable int movieId) {
        movieService.refreshMovieCache(movieId);
        return new ApiResponse<>("200", "Movie cache refreshed for ID: " + movieId, null);
    }
    
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Cache statistics available in analytics endpoint");
        return new ApiResponse<>("200", "Cache stats endpoint", stats);
    }
    
}