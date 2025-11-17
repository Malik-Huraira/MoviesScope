package com.moviescope.dto.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MovieByKeywordRequest {
    private int keywordId;
    private int page = 1;
}
