package com.moviescope.service;

import java.util.List;

import com.moviescope.dto.response.MovieAnalyticsResponse;
import com.moviescope.dto.response.MovieDTO;
import com.moviescope.entity.MovieEntity;

public interface MovieService {

    List<MovieDTO> getPopularMovies();

    List<MovieDTO> getMoviesByKeyword(int keywordId);

    List<MovieDTO> getMoviesByKeyword(int keywordId, boolean fetchDetails);

    List<MovieDTO> searchMovies(String query);

    List<MovieDTO> searchMoviesByTitle(String title);

    MovieDTO getMovieDetails(int movieId);

    MovieEntity fetchMovieEntity(int movieId);

    MovieAnalyticsResponse getMovieAnalytics();

}
