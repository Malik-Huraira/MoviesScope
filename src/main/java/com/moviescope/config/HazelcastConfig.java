package com.moviescope.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MaxSizePolicy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class HazelcastConfig {

    @Bean
    public Config hazelcastConfiguration() {
        Config config = new Config();
        config.setInstanceName("movie-hazelcast-instance");

        // Example cache map
        config.addMapConfig(createMapConfig("movies-cache", 3600, 1800, 1000));

        return config;
    }

    private MapConfig createMapConfig(String name, int ttlSeconds, int maxIdleSeconds, int maxSize) {
        MapConfig mapConfig = new MapConfig();
        mapConfig.setName(name);
        mapConfig.setTimeToLiveSeconds(ttlSeconds);
        mapConfig.setMaxIdleSeconds(maxIdleSeconds);
        mapConfig.setEvictionConfig(new EvictionConfig()
                .setEvictionPolicy(EvictionPolicy.LRU)
                .setMaxSizePolicy(MaxSizePolicy.PER_NODE)
                .setSize(maxSize));
        return mapConfig;
    }

    @Bean
    public HazelcastInstance hazelcastInstance(Config config) {
        return com.hazelcast.core.Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }
}
