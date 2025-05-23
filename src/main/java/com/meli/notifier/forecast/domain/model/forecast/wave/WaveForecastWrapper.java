package com.meli.notifier.forecast.domain.model.forecast.wave;

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
public class WaveForecastWrapper implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String updateDate;
    private WaveForecast morning;
    private WaveForecast afternoon;
    private WaveForecast evening;
}
