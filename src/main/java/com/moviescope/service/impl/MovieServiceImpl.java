package com.moviescope.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviescope.dto.response.MovieAnalyticsResponse;
import com.moviescope.dto.response.MovieDTO;
import com.moviescope.entity.MovieEntity;
import com.moviescope.repository.MovieRepository;
import com.moviescope.service.MovieService;
import com.moviescope.utils.TMDBApiConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private final RestTemplate restTemplate;
    private final TMDBApiConstants tmdbApiConstants;
    private final MovieRepository movieRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public MovieServiceImpl(
            RestTemplate restTemplate,
            TMDBApiConstants tmdbApiConstants,
            MovieRepository movieRepository) {
        this.restTemplate = restTemplate;
        this.tmdbApiConstants = tmdbApiConstants;
        this.movieRepository = movieRepository;
    }

    // GET POPULAR MOVIES

    /**
     * Fetch popular movies (first 5 pages) and save in DB
     */
    @Override
    @Transactional
    public List<MovieDTO> getPopularMovies() {
        List<MovieDTO> movies = new ArrayList<>();

        try {
            for (int page = 1; page <= 5; page++) {
                String url = UriComponentsBuilder
                        .fromUriString(tmdbApiConstants.getPopularBaseUrl())
                        .queryParam("api_key", tmdbApiConstants.getApiKey())
                        .queryParam("language", "en-US")
                        .queryParam("page", page)
                        .build()
                        .toUriString();

                String response = restTemplate.getForObject(url, String.class);
                JsonNode results = mapper.readTree(response).path("results");

                for (JsonNode node : results) {
                    int movieId = node.path("id").asInt();

                    // Check if movie exists in DB
                    MovieEntity movieEntity = movieRepository.findById(movieId).orElseGet(() -> {
                        MovieEntity entity = fetchMovieEntity(movieId);
                        if (entity != null)
                            movieRepository.save(entity);
                        return entity;
                    });

                    if (movieEntity != null)
                        movies.add(toDTO(movieEntity));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return movies;
    }

    // GET MOVIES BY KEYWORD

    /**
     * Fetch movies by keyword with optional detailed info
     */
    @Override
    @Transactional
    public List<MovieDTO> getMoviesByKeyword(int keywordId, boolean fetchDetails) {
        List<MovieDTO> movies = new ArrayList<>();

        try {
            int totalPages = 1;

            for (int page = 1; page <= totalPages; page++) {
                String keywordUrl = UriComponentsBuilder
                        .fromUriString(tmdbApiConstants.getKeywordMoviesUrl(keywordId))
                        .queryParam("api_key", tmdbApiConstants.getApiKey())
                        .queryParam("language", "en-US")
                        .queryParam("page", page)
                        .build()
                        .toUriString();

                String response = restTemplate.getForObject(keywordUrl, String.class);
                JsonNode root = mapper.readTree(response);
                JsonNode results = root.path("results");

                if (page == 1) {
                    totalPages = Math.min(root.path("total_pages").asInt(), 5); // max 5 pages
                }

                if (fetchDetails) {
                    List<CompletableFuture<MovieDTO>> futures = new ArrayList<>();
                    for (JsonNode node : results) {
                        int movieId = node.path("id").asInt();
                        futures.add(CompletableFuture.supplyAsync(() -> {
                            MovieEntity entity = movieRepository.findById(movieId)
                                    .orElseGet(() -> {
                                        MovieEntity newEntity = fetchMovieEntity(movieId);
                                        if (newEntity != null)
                                            movieRepository.save(newEntity);
                                        return newEntity;
                                    });
                            return entity != null ? toDTO(entity) : null;
                        }));
                    }
                    movies.addAll(futures.stream()
                            .map(CompletableFuture::join)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()));
                } else {
                    // Summary info only
                    for (JsonNode node : results) {
                        movies.add(MovieDTO.builder()
                                .title(node.path("title").asText())
                                .overview(node.path("overview").asText())
                                .releaseDate(node.path("release_date").asText())
                                .genres(null)
                                .runtime(0)
                                .rating(0.0)
                                .build());
                    }
                }

                if (results.isEmpty())
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movies;
    }

    // Simple overload

    @Override
    public List<MovieDTO> getMoviesByKeyword(int keywordId) {
        return getMoviesByKeyword(keywordId, true);
    }

    // Fetch detailed info from TMDb and convert to MovieEntity

    /**
     * Fetch detailed info from TMDb and convert to MovieEntity
     */
    public MovieEntity fetchMovieEntity(int movieId) {
        try {
            String movieUrl = UriComponentsBuilder
                    .fromUriString(tmdbApiConstants.getMovieDetailsBaseUrl(movieId))
                    .queryParam("api_key", tmdbApiConstants.getApiKey())
                    .queryParam("language", "en-US")
                    .build()
                    .toUriString();

            String response = restTemplate.getForObject(movieUrl, String.class);
            JsonNode node = mapper.readTree(response);

            List<String> genres = new ArrayList<>();
            for (JsonNode genreNode : node.path("genres")) {
                genres.add(genreNode.path("name").asText());
            }

            return MovieEntity.builder()
                    .id(movieId)
                    .title(node.path("title").asText())
                    .overview(node.path("overview").asText())
                    .releaseDate(node.path("release_date").asText())
                    .genres(String.join(",", genres))
                    .runtime(node.path("runtime").asInt(0))
                    .rating(node.path("vote_average").asDouble(0.0))
                    .favorite(false)
                    .build();

        } catch (Exception e) {
            System.err.println("Error fetching details for movie ID " + movieId);
            return null;
        }
    }

    // Search movies by query (local DB + TMDB)

    @Override
    public List<MovieDTO> searchMovies(String query) {
        // First search local database
        List<MovieDTO> localResults = searchMoviesByTitle(query);

        // Then search TMDB for more comprehensive results
        List<MovieDTO> tmdbResults = searchMoviesFromTMDB(query);

        // Combine and remove duplicates
        return combineResults(localResults, tmdbResults);
    }

    // Fixed: Proper return type
    private List<MovieDTO> searchMoviesFromTMDB(String query) {
        List<MovieDTO> movies = new ArrayList<>();

        try {
            String searchUrl = UriComponentsBuilder
                    .fromUriString(tmdbApiConstants.getSearchMoviesUrl())
                    .queryParam("api_key", tmdbApiConstants.getApiKey())
                    .queryParam("query", query)
                    .queryParam("language", "en-US")
                    .build()
                    .toUriString();

            String response = restTemplate.getForObject(searchUrl, String.class);
            JsonNode results = mapper.readTree(response).path("results");

            for (JsonNode node : results) {
                int movieId = node.path("id").asInt();

                MovieEntity movieEntity = movieRepository.findById(movieId).orElseGet(() -> {
                    MovieEntity entity = fetchMovieEntity(movieId);
                    if (entity != null)
                        movieRepository.save(entity);
                    return entity;
                });

                if (movieEntity != null) {
                    movies.add(toDTO(movieEntity));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return movies;
    }

    private List<MovieDTO> combineResults(List<MovieDTO> local, List<MovieDTO> tmdb) {
        Map<Integer, MovieDTO> uniqueMovies = new LinkedHashMap<>();

        // Use movie ID instead of title hash
        for (MovieDTO movie : local) {
            if (movie.getId() != null) {
                uniqueMovies.put(movie.getId(), movie);
            }
        }

        for (MovieDTO movie : tmdb) {
            if (movie.getId() != null) {
                uniqueMovies.putIfAbsent(movie.getId(), movie);
            }
        }

        return new ArrayList<>(uniqueMovies.values());
    }

    // Fixed: Proper return type
    @Override
    public MovieDTO getMovieDetails(int movieId) {
        MovieEntity movieEntity = movieRepository.findById(movieId)
                .orElseGet(() -> {
                    MovieEntity newEntity = fetchMovieEntity(movieId);
                    if (newEntity != null)
                        movieRepository.save(newEntity);
                    return newEntity;
                });

        return movieEntity != null ? toDTO(movieEntity) : null;
    }

    // Fixed: Proper generic types in return
   // Update the getMovieAnalytics method
@Override
public MovieAnalyticsResponse getMovieAnalytics() {
    List<MovieEntity> allMovies = movieRepository.findAll();
    
    // Calculate basic analytics
    double averageRating = allMovies.stream()
            .mapToDouble(MovieEntity::getRating)
            .average().orElse(0.0);
    
    Map<String, Long> moviesPerGenre = allMovies.stream()
            .flatMap(m -> Arrays.stream(Optional.ofNullable(m.getGenres())
                    .orElse("").split(",")))
            .filter(genre -> !genre.trim().isEmpty())
            .collect(Collectors.groupingBy(
                    genre -> genre.trim(),
                    Collectors.counting()));
    
    // You might want to inject repositories to get user analytics
    // For now, we'll return basic analytics
    
    return MovieAnalyticsResponse.builder()
            .totalMovies(allMovies.size())
            .averageRating(Math.round(averageRating * 10.0) / 10.0)
            .moviesPerGenre(moviesPerGenre)
            .totalFavorites(0L) // You can calculate this from UserFavoriteRepository
            .totalReviews(0L)   // You can calculate this from MovieReviewRepository
            .totalRatings(0L)   // You can calculate this from UserRatingRepository
            .build();
}

    /**
     * Convert MovieEntity to DTO
     */
    private MovieDTO toDTO(MovieEntity entity) {
        List<String> genres = entity.getGenres() != null && !entity.getGenres().isEmpty()
                ? Arrays.asList(entity.getGenres().split(","))
                : Collections.emptyList();

        return MovieDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .overview(entity.getOverview())
                .releaseDate(entity.getReleaseDate())
                .genres(genres)
                .runtime(entity.getRuntime())
                .rating(entity.getRating())
                .favorite(entity.getFavorite())
                .build();
    }

    /**
     * Additional features: search movies by title
     */
    public List<MovieDTO> searchMoviesByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}