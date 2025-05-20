package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.domain.model.database.City;
import com.meli.notifier.forecast.domain.model.forecast.CombinedForecastDTO;

import java.util.List;

public interface CptecService {

    CombinedForecastDTO getCombinedForecast(Long cityId);

    List<City> findCities(String cityName);
}
