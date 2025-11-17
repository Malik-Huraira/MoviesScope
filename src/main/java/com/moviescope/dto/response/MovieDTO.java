package com.moviescope.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@AllArgsConstructor
public class MovieDTO {
    @JsonProperty("MovieId")
    private Integer id;
    @JsonProperty("MovieTitle")
    private String title;
    @JsonProperty("MovieOverview")
    private String overview;
    @JsonProperty("MovieReleaseDate")
    private String releaseDate;

    // Extra fields for detailed info
    @JsonProperty("Genres")
    private List<String> genres;
    // e.g., ["Action", "Drama"]
    @JsonProperty("Runtime")
    private int runtime;
    // in minutes
    @JsonProperty("Rating")
    private double rating;
    // vote_average

    private Boolean favorite;
    private Double userRating;
}
