package com.moviescope.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> appDetails = new HashMap<>();
        appDetails.put("name", "MovieScope");
        appDetails.put("version", "1.0.0");
        appDetails.put("description", "Movie Management System");
        appDetails.put("author", "Your Name");

        Map<String, Object> techStack = new HashMap<>();
        techStack.put("backend", "Spring Boot 3.x");
        techStack.put("database", "MySQL 8.0");
        techStack.put("cache", "Redis");
        techStack.put("authentication", "JWT");

        Map<String, Object> features = new HashMap<>();
        features.put("movie-discovery", "TMDB Integration");
        features.put("user-management", "Authentication & Authorization");
        features.put("ratings-reviews", "User Interactions");
        features.put("admin-dashboard", "Administration Panel");

        builder.withDetail("application", appDetails)
                .withDetail("technology", techStack)
                .withDetail("features", features)
                .withDetail("status", "ACTIVE");
    }
}