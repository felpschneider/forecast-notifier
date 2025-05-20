package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.domain.model.database.City;

import java.util.List;
import java.util.Optional;

public interface CityService {
    List<City> findCities(String cityName);

    List<City> saveCitiesToDatabase(List<City> cities);

    void saveCity(City city);

    void saveCity(Long id);

    Optional<City> findById(Long cityId);
}
