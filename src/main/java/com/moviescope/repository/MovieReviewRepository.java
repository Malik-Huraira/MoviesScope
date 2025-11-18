package com.moviescope.repository;

import com.moviescope.entity.MovieReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MovieReviewRepository extends JpaRepository<MovieReview, Long> {
    List<MovieReview> findByMovieIdOrderByCreatedAtDesc(Integer movieId);

    List<MovieReview> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT mr FROM MovieReview mr WHERE mr.movie.id = :movieId ORDER BY mr.createdAt DESC")
    List<MovieReview> findReviewsByMovieIdWithUser(@Param("movieId") Integer movieId);

    boolean existsByUserIdAndMovieId(Long userId, Integer movieId);
}