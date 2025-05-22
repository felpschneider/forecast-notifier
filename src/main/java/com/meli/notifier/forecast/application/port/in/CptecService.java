package com.meli.notifier.forecast.application.port.in;

import com.meli.notifier.forecast.domain.model.database.City;
import com.meli.notifier.forecast.domain.model.forecast.CombinedForecastDTO;
import com.meli.notifier.forecast.domain.model.forecast.wave.WaveForecastResponseDTO;
import com.meli.notifier.forecast.domain.model.forecast.weather.ForecastResponseDTO;

import java.util.List;

public interface CptecService {

    List<City> findCities(String cityName);

    CombinedForecastDTO getCombinedForecast(Long cityId);

    ForecastResponseDTO getWeatherForecast(Long cityId);

    WaveForecastResponseDTO getWaveForecast(Long cityId, int day);
}
