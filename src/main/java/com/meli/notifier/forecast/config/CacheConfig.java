package com.meli.notifier.forecast.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableCaching
public class CacheConfig {

    private static final String WEATHER_CACHE = "weatherCache";
    private static final String CITIES_CACHE = "citiesCache";
    private static final String WAVE_FORECAST_CACHE = "waveForecastCache";

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(WEATHER_CACHE, createCacheConfiguration(getDurationUntilEndOfDay()));
        cacheConfigurations.put(CITIES_CACHE, createCacheConfiguration(getDurationUntilEndOfDay()));
        cacheConfigurations.put(WAVE_FORECAST_CACHE, createCacheConfiguration(getDurationUntilEndOfDay()));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    private RedisCacheConfiguration createCacheConfiguration(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .disableCachingNullValues();
    }

    private Duration getDurationUntilEndOfDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.with(LocalTime.MAX);
        return Duration.between(now, endOfDay);
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("customCache",
                        createCacheConfiguration(Duration.ofMinutes(5)));
    }
}
