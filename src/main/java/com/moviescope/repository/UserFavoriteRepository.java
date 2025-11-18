package com.moviescope.repository;

import com.moviescope.entity.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    Optional<UserFavorite> findByUserIdAndMovieId(Long userId, Integer movieId);

    List<UserFavorite> findByUserId(Long userId);

    boolean existsByUserIdAndMovieId(Long userId, Integer movieId);

    @Query("SELECT COUNT(uf) FROM UserFavorite uf WHERE uf.movie.id = :movieId")
    Long countByMovieId(@Param("movieId") Integer movieId);

    void deleteByUserIdAndMovieId(Long userId, Integer movieId);
}