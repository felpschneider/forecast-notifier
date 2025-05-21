package com.meli.notifier.forecast.domain.model.forecast;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinedForecastDTO {
    private String cityName;
    private String stateCode;
    private WeatherForecastWrapper weatherForecast;
    private WaveForecastWrapper waveForecast;
}
