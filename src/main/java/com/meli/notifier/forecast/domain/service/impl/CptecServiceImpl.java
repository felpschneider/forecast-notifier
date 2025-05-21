package com.meli.notifier.forecast.domain.service.impl;

import com.meli.notifier.forecast.adapter.integration.client.CptecFeignClient;
import com.meli.notifier.forecast.adapter.integration.model.wave.WaveForecastResponseDTO;
import com.meli.notifier.forecast.adapter.integration.model.weather.ForecastResponseDTO;
import com.meli.notifier.forecast.application.mapper.ForecastMapper;
import com.meli.notifier.forecast.domain.exception.ServiceUnavailableException;
import com.meli.notifier.forecast.domain.mapper.CityMapper;
import com.meli.notifier.forecast.domain.model.database.City;
import com.meli.notifier.forecast.domain.model.forecast.CombinedForecastDTO;
import com.meli.notifier.forecast.domain.service.CityService;
import com.meli.notifier.forecast.domain.service.CptecService;
import feign.FeignException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CptecServiceImpl implements CptecService {

    private final CptecFeignClient cptecClient;
    private final CityService cityService;
    private final CityMapper cityMapper;
    private final ForecastMapper forecastMapper;

    @Override
    @Cacheable(value = "citiesCache", key = "#cityName")
    @Retry(name = "cptecRetry")
    public List<City> findCities(String cityName) {
        try {
            var response = cptecClient.findCities(cityName);

            if (response == null || response.getCities() == null) {
                log.warn("No cities found for search term: {}", cityName);
                return List.of();
            }

            return response.getCities().stream()
                    .map(cityMapper::toModel)
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            throw cptecApiError("Error calling CPTEC API for forecast: {}", e);
        }
    }

    @Override
    @Cacheable(value = "weatherCache", key = "#cityId")
//    @Retry(name = "cptecRetry", fallbackMethod = "getForecastFallback")
    public CombinedForecastDTO getCombinedForecast(Long cityId) {
        ForecastResponseDTO weatherResponse = getForecast(cityId);
        int today = 0;

        WaveForecastResponseDTO waveForecast = null;
        if (isCityCoastal(cityId)) {
            waveForecast = getWaveForecast(cityId, today);
        }

        return CombinedForecastDTO.builder()
                .cityName(weatherResponse.getName())
                .stateCode(weatherResponse.getStateCode())
                .weatherForecast(forecastMapper.toWrapper(weatherResponse))
                .waveForecast(waveForecast != null ? forecastMapper.toWrapper(waveForecast) : null)
                .build();
    }

    @Cacheable(value = "weatherCache", key = "'weather:' + #cityId")
    public ForecastResponseDTO getForecast(Long cityId) {
        try {
            return cptecClient.getForecast(cityId);
        } catch (FeignException e) {
            throw cptecApiError("Error calling CPTEC API for forecast: {}", e);
        }
    }

    @Cacheable(value = "waveForecastCache", key = "'wave:' + #cityId + ':' + #day")
    public WaveForecastResponseDTO getWaveForecast(Long cityId, int day) {
        try {
            return cptecClient.getWaveForecast(cityId, day);
        } catch (FeignException e) {
            throw cptecApiError("Error calling CPTEC API for forecast: {}", e);
        }
    }

    private static ServiceUnavailableException cptecApiError(String format, FeignException e) {
        log.error(format, e.getMessage());
        return new ServiceUnavailableException("CPTEC API is currently unavailable");
    }

    @Transactional
    public Boolean isCityCoastal(Long cityId) {
        Optional<City> cityOpt = cityService.findById(cityId);

        if (cityOpt.isPresent() && cityOpt.get().getIsCoastal() != null) {
            var city = cityOpt.get();
            return city.getIsCoastal();
        }

        City city = cityOpt.get();

        var waveForecast = getWaveForecast(cityId, 0);

        if (waveForecast == null || waveForecast.getName().equalsIgnoreCase("undefined")) {
            log.warn("City with id: {} is not coastal", cityId);
            city.setIsCoastal(false);
            cityService.saveCity(city);
            return false;
        }

        log.info("City {} with id: {} is coastal", waveForecast.getName(), cityId);
        city.setIsCoastal(true);
        cityService.saveCity(cityId);
        return true;
    }
}
