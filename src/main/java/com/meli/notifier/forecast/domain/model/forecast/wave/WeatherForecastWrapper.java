package com.meli.notifier.forecast.domain.model.forecast.wave;

import com.meli.notifier.forecast.domain.model.forecast.weather.ForecastDay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecastWrapper implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String updateDate;
    private List<ForecastDay> forecastDays;
}
