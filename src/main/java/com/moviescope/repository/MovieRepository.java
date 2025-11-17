package com.moviescope.repository;

import com.moviescope.entity.MovieEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<MovieEntity, Integer> {

    List<MovieEntity> findByTitleContainingIgnoreCase(String title);

}
