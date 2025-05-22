package com.meli.notifier.forecast.application.service;

import com.meli.notifier.forecast.adapter.out.integration.client.CptecFeignClient;
import com.meli.notifier.forecast.application.port.in.CityService;
import com.meli.notifier.forecast.application.port.in.CoastalCityService;
import com.meli.notifier.forecast.domain.model.database.City;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoastalCityServiceImpl implements CoastalCityService {

    private final CptecFeignClient cptecClient;
    private final CityService cityService;

    @Override
    @Transactional
    public Boolean isCityCoastal(Long cityId) {
        Optional<City> cityOpt = cityService.findById(cityId);

        if (cityOpt.isEmpty()) {
            log.warn("City not found with id: {}", cityId);
            return false;
        }

        City city = cityOpt.get();
        if (city.getIsCoastal() != null) {
            return city.getIsCoastal();
        }

        try {
            var waveForecast = cptecClient.getWaveForecast(cityId, 0);
            boolean isCoastal = waveForecast != null && !waveForecast.getName().equalsIgnoreCase(city.getName());

            city.setIsCoastal(isCoastal);
            cityService.saveCity(city);

            if (isCoastal) {
                log.info("City {} with id: {} is coastal", waveForecast.getName(), cityId);
            } else {
                log.warn("City with id: {} is not coastal", cityId);
            }

            return isCoastal;
        } catch (Exception e) {
            log.error("Error determining if city is coastal: {}", e.getMessage());
            throw e;
        }
    }
}
