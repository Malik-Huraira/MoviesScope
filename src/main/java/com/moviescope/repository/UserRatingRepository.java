package com.moviescope.repository;

import com.moviescope.entity.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRatingRepository extends JpaRepository<UserRating, Long> {
    Optional<UserRating> findByUserIdAndMovieId(Long userId, Integer movieId);

    List<UserRating> findByUserId(Long userId);

    List<UserRating> findByMovieId(Integer movieId);

    @Query("SELECT AVG(ur.rating) FROM UserRating ur WHERE ur.movie.id = :movieId")
    Double findAverageRatingByMovieId(@Param("movieId") Integer movieId);

    @Query("SELECT COUNT(ur) FROM UserRating ur WHERE ur.movie.id = :movieId")
    Long countByMovieId(@Param("movieId") Integer movieId);
}