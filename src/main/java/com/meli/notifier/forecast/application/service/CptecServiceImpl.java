package com.meli.notifier.forecast.application.service;

import com.meli.notifier.forecast.adapter.out.integration.client.CptecFeignClient;
import com.meli.notifier.forecast.adapter.out.integration.model.wave.WaveForecastResponseDTO;
import com.meli.notifier.forecast.adapter.out.integration.model.weather.ForecastResponseDTO;
import com.meli.notifier.forecast.application.port.in.CityService;
import com.meli.notifier.forecast.application.port.in.CoastalCityService;
import com.meli.notifier.forecast.application.port.in.CptecService;
import com.meli.notifier.forecast.domain.exception.BadRequestException;
import com.meli.notifier.forecast.domain.exception.ServiceUnavailableException;
import com.meli.notifier.forecast.domain.mapper.CityMapper;
import com.meli.notifier.forecast.domain.mapper.ForecastMapper;
import com.meli.notifier.forecast.domain.model.database.City;
import com.meli.notifier.forecast.domain.model.forecast.CombinedForecastDTO;
import feign.FeignException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Service
@Slf4j
@RequiredArgsConstructor
public class CptecServiceImpl implements CptecService {

    private final CptecFeignClient cptecClient;
    private final CoastalCityService coastalCityService;
    private final CityService cityService;
    private final CityMapper cityMapper;
    private final ForecastMapper forecastMapper;
    private final RedisTemplate<String, String> redisTemplate;

    //    @Cacheable(value = "citiesCache", key = "#cityName")
    @Retry(name = "cptecRetry")
    @Override
    public List<City> findCities(String cityName) {
        try {
            var citiesFromDb = cityService.findCities(cityName);
            var cityNameFoundInList = citiesFromDb.stream().anyMatch(c -> c.getName().equalsIgnoreCase(cityName));

            if (!citiesFromDb.isEmpty() && cityNameFoundInList) {
                log.info("Cities found in database for name: {}", cityName);
                return citiesFromDb;
            }

            var response = cptecClient.findCities(cityName);
            if (response == null || isEmpty(response.getCities())) {
                log.warn("No cities found in CPTEC API for city name: {}", cityName);
                return List.of();
            }

            return response.getCities().stream()
                    .map(cityMapper::toModel)
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            throw handleCptecApiError("Error searching cities", e);
        }
    }

    @Cacheable(value = "weatherCache", key = "#cityId")
    @Retry(name = "cptecRetry")
    @Override
    public CombinedForecastDTO getCombinedForecast(Long cityId) {
        try {
            ForecastResponseDTO weatherResponse = getWeatherForecast(cityId);
            WaveForecastResponseDTO waveForecast = getWaveForecastIfCoastal(cityId);

            return buildCombinedForecast(weatherResponse, waveForecast);
        } catch (FeignException e) {
            throw handleCptecApiError("Error getting combined forecast", e);
        }
    }

    @Cacheable(value = "weatherCache", key = "'weather:' + #cityId")
    @Override
    public ForecastResponseDTO getWeatherForecast(Long cityId) {
        try {
            return cptecClient.getForecast(cityId);
        } catch (FeignException e) {
            throw handleCptecApiError("Error getting weather forecast", e);
        }
    }

    @Cacheable(value = "waveForecastCache", key = "'wave:' + #cityId + ':' + #day")
    @Override
    public WaveForecastResponseDTO getWaveForecast(Long cityId, int day) {
        if (day < 0 || day > 3) {
            throw new BadRequestException("Day must be between 0 and 3");
        }

        try {
            var waveForecast = cptecClient.getWaveForecast(cityId, day);
            return waveForecast.getName().equalsIgnoreCase("undefined") ? null : waveForecast;
        } catch (FeignException e) {
            throw handleCptecApiError("Error getting wave forecast", e);
        }
    }

    private WaveForecastResponseDTO getWaveForecastIfCoastal(Long cityId) {
        return coastalCityService.isCityCoastal(cityId) ? getWaveForecast(cityId, 0) : null;
    }

    private CombinedForecastDTO buildCombinedForecast(ForecastResponseDTO weatherResponse, WaveForecastResponseDTO waveForecast) {
        return CombinedForecastDTO.builder()
                .cityName(weatherResponse.getName())
                .stateCode(weatherResponse.getStateCode())
                .weatherForecast(forecastMapper.toWrapper(weatherResponse))
                .waveForecast(waveForecast != null ? forecastMapper.toWrapper(waveForecast) : null)
                .build();
    }

    private ServiceUnavailableException handleCptecApiError(String message, FeignException e) {
        log.error("{}: {}", message, e.getMessage());
        return new ServiceUnavailableException("CPTEC API is currently unavailable");
    }
}
