package com.meli.notifier.forecast.domain.model.forecast;

import com.meli.notifier.forecast.domain.model.forecast.wave.WaveForecastWrapper;
import com.meli.notifier.forecast.domain.model.forecast.wave.WeatherForecastWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinedForecastDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String cityName;
    private String stateCode;
    private WeatherForecastWrapper weatherForecast;
    private WaveForecastWrapper waveForecast;
}
