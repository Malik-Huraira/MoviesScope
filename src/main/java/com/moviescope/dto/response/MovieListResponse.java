package com.moviescope.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MovieListResponse {
    private List<MovieDTO> movies;
}
