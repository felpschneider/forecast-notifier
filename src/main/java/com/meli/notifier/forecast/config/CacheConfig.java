package com.meli.notifier.forecast.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
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
    private static final String WAVE_FORECAST_CACHE = "waveForecastCache";    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Use a more specific typing configuration to avoid serialization issues with collections
        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(), 
            ObjectMapper.DefaultTyping.NON_FINAL
        );
        return mapper;
    }    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.setEnableDefaultSerializer(false);
        template.afterPropertiesSet();

        return template;
    }    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        // Create a custom ObjectMapper with datetime support and type information
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper());

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(WEATHER_CACHE, createCacheConfiguration(getDurationUntilEndOfDay()));
        cacheConfigurations.put(CITIES_CACHE, createCacheConfiguration(getDurationUntilEndOfDay()));
        cacheConfigurations.put(WAVE_FORECAST_CACHE, createCacheConfiguration(getDurationUntilEndOfDay()));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration())
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    private RedisCacheConfiguration createCacheConfiguration(Duration ttl) {
        return cacheConfiguration().entryTtl(ttl);
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
