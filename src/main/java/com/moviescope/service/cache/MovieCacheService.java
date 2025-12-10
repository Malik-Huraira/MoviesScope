package com.moviescope.service.cache;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class MovieCacheService {

    private final HazelcastInstance hazelcastInstance;
    
    private static final String MOVIES_CACHE = "movies-cache";
    private static final String POPULAR_MOVIES_CACHE = "popular-movies-cache";
    private static final String KEYWORD_MOVIES_CACHE = "keyword-movies-cache";
    private static final String SEARCH_CACHE = "search-cache";
    private static final String ANALYTICS_CACHE = "analytics-cache";
    
    public MovieCacheService(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
    
    // Movie details cache
    public void cacheMovieDetails(Integer movieId, Object movieDetails) {
        IMap<Integer, Object> cache = hazelcastInstance.getMap(MOVIES_CACHE);
        cache.put(movieId, movieDetails, 1, TimeUnit.HOURS);
    }
    
    public Object getCachedMovieDetails(Integer movieId) {
        IMap<Integer, Object> cache = hazelcastInstance.getMap(MOVIES_CACHE);
        return cache.get(movieId);
    }
    
    public boolean isMovieCached(Integer movieId) {
        IMap<Integer, Object> cache = hazelcastInstance.getMap(MOVIES_CACHE);
        return cache.containsKey(movieId);
    }
    
    public void evictMovieFromCache(Integer movieId) {
        IMap<Integer, Object> cache = hazelcastInstance.getMap(MOVIES_CACHE);
        cache.delete(movieId);
    }
    
    // Popular movies cache
    public void cachePopularMovies(Object movies) {
        IMap<String, Object> cache = hazelcastInstance.getMap(POPULAR_MOVIES_CACHE);
        cache.put("popular", movies, 30, TimeUnit.MINUTES);
    }
    
    public Object getCachedPopularMovies() {
        IMap<String, Object> cache = hazelcastInstance.getMap(POPULAR_MOVIES_CACHE);
        return cache.get("popular");
    }
    
    // Keyword movies cache
    public void cacheKeywordMovies(Integer keywordId, Object movies) {
        IMap<String, Object> cache = hazelcastInstance.getMap(KEYWORD_MOVIES_CACHE);
        cache.put("keyword_" + keywordId, movies, 2, TimeUnit.HOURS);
    }
    
    public Object getCachedKeywordMovies(Integer keywordId) {
        IMap<String, Object> cache = hazelcastInstance.getMap(KEYWORD_MOVIES_CACHE);
        return cache.get("keyword_" + keywordId);
    }
    
    // Search cache
    public void cacheSearchResults(String query, Object results) {
        IMap<String, Object> cache = hazelcastInstance.getMap(SEARCH_CACHE);
        cache.put("search_" + query.hashCode(), results, 1, TimeUnit.HOURS);
    }
    
    public Object getCachedSearchResults(String query) {
        IMap<String, Object> cache = hazelcastInstance.getMap(SEARCH_CACHE);
        return cache.get("search_" + query.hashCode());
    }
    
    // Analytics cache
    public void cacheAnalytics(Object analytics) {
        IMap<String, Object> cache = hazelcastInstance.getMap(ANALYTICS_CACHE);
        cache.put("analytics", analytics, 10, TimeUnit.MINUTES);
    }
    
    public Object getCachedAnalytics() {
        IMap<String, Object> cache = hazelcastInstance.getMap(ANALYTICS_CACHE);
        return cache.get("analytics");
    }
    
    public long getCachedMoviesCount() {
        IMap<Integer, Object> cache = hazelcastInstance.getMap(MOVIES_CACHE);
        return cache.size();
    }
    
    // Clear all caches
    public void clearAllCaches() {
        hazelcastInstance.getMap(MOVIES_CACHE).clear();
        hazelcastInstance.getMap(POPULAR_MOVIES_CACHE).clear();
        hazelcastInstance.getMap(KEYWORD_MOVIES_CACHE).clear();
        hazelcastInstance.getMap(SEARCH_CACHE).clear();
        hazelcastInstance.getMap(ANALYTICS_CACHE).clear();
    }
    
    // Cache statistics
    public Object getCacheStatistics() {
        return hazelcastInstance.getMap(MOVIES_CACHE).getLocalMapStats();
    }
}