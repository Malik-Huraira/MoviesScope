package com.moviescope.config;

import com.moviescope.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/movies/popular").permitAll()
                        .requestMatchers("/api/movies/search").permitAll()
                        .requestMatchers("/api/movies/{movieId}").permitAll()
                        .requestMatchers("/api/movies/keyword/**").permitAll()
                        .requestMatchers("/api/movies/{movieId}/average-rating").permitAll()
                        .requestMatchers("/api/movies/{movieId}/reviews").permitAll()
                        .requestMatchers("/api/movies/analytics").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/api/debug/**").permitAll()
                        // Authenticated endpoints
                        .requestMatchers(HttpMethod.POST, "/api/movies/*/favorite").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/movies/*/favorite").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/movies/*/favorite").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/movies/*/rating").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/movies/*/rating").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/movies/*/reviews").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/movies/reviews/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/movies/reviews/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/movies/users/*/reviews").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/movies/profile").authenticated()

                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Any other request
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}