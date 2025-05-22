package com.meli.notifier.forecast.domain.model.forecast;

import com.meli.notifier.forecast.domain.model.forecast.weather.ForecastDay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecastWrapper {
    private String updateDate;
    private List<ForecastDay> forecastDays;
}
