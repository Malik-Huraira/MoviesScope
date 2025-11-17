package com.moviescope.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TMDBApiConstants {

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.base-url}")
    private String baseUrl;

    public String getApiKey() {
        return apiKey;
    }

    String getBaseUrl() {
        return baseUrl;
    }

    public String getPopularBaseUrl() {
        return baseUrl + "/movie/popular";
    }

    public String getKeywordMoviesUrl(int keywordId) {
        return baseUrl + "/keyword/" + keywordId + "/movies";

    }

    public String getMovieDetailsBaseUrl(int movieId) {
        return baseUrl + "/movie/" + movieId;
    }

    public String getSearchMoviesUrl() {
        return baseUrl + "/search/movie";
    }
}
