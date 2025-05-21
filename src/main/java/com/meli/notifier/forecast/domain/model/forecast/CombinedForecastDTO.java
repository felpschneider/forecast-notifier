package com.meli.notifier.forecast.domain.model.forecast;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinedForecastDTO {
    private String cityName;
    private String stateCode;
    private WeatherForecastWrapper weatherForecast;
    private WaveForecastWrapper waveForecast;
    
    // Additional fields for simplified representation
    private Double temperature;
    private WeatherCondition condition;
    private List<DailyForecastDTO> forecasts;
}
