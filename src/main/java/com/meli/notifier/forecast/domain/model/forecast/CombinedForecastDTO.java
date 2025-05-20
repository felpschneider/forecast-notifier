package com.meli.notifier.forecast.domain.model.forecast;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinedForecastDTO {
    private String cityName;
    private String stateCode;
    private LocalDate updateDate;
    private WeatherForecastWrapper weatherForecast;
    private WaveForecastWrapper waveForecast;
}
