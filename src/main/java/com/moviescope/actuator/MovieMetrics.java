package com.moviescope.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieMetrics {

    private final MeterRegistry meterRegistry;

    public void incrementMovieViews(int movieId) {
        Counter.builder("moviescope.movie.views")
                .tag("movieId", String.valueOf(movieId))
                .register(meterRegistry)
                .increment();
    }

    public void incrementSearchCount(String query) {
        Counter.builder("moviescope.search.count")
                .tag("query", query)
                .register(meterRegistry)
                .increment();
    }

    public void incrementApiCall(String endpoint) {
        Counter.builder("moviescope.api.calls")
                .tag("endpoint", endpoint)
                .register(meterRegistry)
                .increment();
    }
}