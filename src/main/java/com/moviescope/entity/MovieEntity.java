package com.moviescope.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieEntity {
    @Id
    private Integer id;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String overview;

    private String releaseDate;

    @Column(columnDefinition = "TEXT")
    private String genres;

    private Integer runtime;
    private Double rating;

    // Default values with @Builder.Default
    @Builder.Default
    private Boolean favorite = false;

    @Builder.Default
    private Double userRating = 0.0;

    
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserFavorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserRating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MovieReview> reviews = new ArrayList<>();
}