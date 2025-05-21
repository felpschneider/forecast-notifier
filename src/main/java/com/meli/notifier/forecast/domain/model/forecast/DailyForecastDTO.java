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
public class DailyForecastDTO {
    private LocalDate date;
    private Integer minTemp;
    private Integer maxTemp;
    private WeatherCondition condition;
}
