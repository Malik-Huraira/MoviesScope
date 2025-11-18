package com.moviescope.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_ratings", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "movie_id" }))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    @Column(nullable = false)
    private Double rating; // 1.0 to 10.0

    @Column(nullable = false)
    private LocalDateTime ratedAt;

    @PrePersist
    protected void onCreate() {
        ratedAt = LocalDateTime.now();
    }
}