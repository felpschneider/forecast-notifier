package com.meli.notifier.forecast.domain.model.forecast;

import com.meli.notifier.forecast.domain.enums.WeatherConditionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecast {
    private String date;
    private WeatherConditionEnum weatherConditionEnum;
    private Integer minTemperature;
    private Integer maxTemperature;
    private Double uvIndex;
}
