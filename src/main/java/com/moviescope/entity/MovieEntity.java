package com.moviescope.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movies")
public class MovieEntity {

    @Id
    private Integer id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String overview;

    private String releaseDate;

    @Column(columnDefinition = "TEXT")
    private String genres; // store as comma-separated

    private Integer runtime;

    private Double rating;

    private Boolean favorite; // user can mark as favorite
    private Double userRating; // user can rate the movie
}
