package com.moviescope.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopularMoviesRequest {
    private int page = 1; // Optional: default page = 1
}