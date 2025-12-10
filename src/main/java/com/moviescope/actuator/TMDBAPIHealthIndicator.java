package com.moviescope.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TMDBAPIHealthIndicator implements HealthIndicator {

    @Value("${tmdb.api.base-url}")
    private String tmdbBaseUrl;

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    private final RestTemplate restTemplate;

    @Override
    public Health health() {
        try {
            String testUrl = tmdbBaseUrl + "/movie/550?api_key=" + tmdbApiKey;
            String response = restTemplate.getForObject(testUrl, String.class);

            if (response != null && response.contains("title")) {
                return Health.up()
                        .withDetail("tmdb-api", "Connected")
                        .withDetail("status", "Healthy")
                        .withDetail("response-time", "OK")
                        .build();
            }
            return Health.down()
                    .withDetail("tmdb-api", "Invalid response")
                    .build();
        } catch (Exception e) {
            log.error("TMDB API health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("tmdb-api", "Connection error")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}