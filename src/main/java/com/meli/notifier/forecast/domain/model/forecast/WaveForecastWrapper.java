package com.meli.notifier.forecast.domain.model.forecast;

import com.meli.notifier.forecast.adapter.out.messaging.integration.model.wave.WaveForecast;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaveForecastWrapper {
    private String updateDate;
    private WaveForecast morning;
    private WaveForecast afternoon;
    private WaveForecast evening;
}
