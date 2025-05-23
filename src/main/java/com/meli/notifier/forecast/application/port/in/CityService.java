package com.meli.notifier.forecast.application.port.in;

import com.meli.notifier.forecast.domain.model.database.City;

import java.util.List;
import java.util.Optional;

public interface CityService {
    List<City> findByName(String cityName);

    List<City> saveCitiesToDatabase(List<City> cities);

    City saveCity(City city);

    Optional<City> findById(Long cityId);

    City saveIfNotExists(City city);
}
