package com.meli.notifier.forecast.domain.model;

import com.meli.notifier.forecast.domain.model.forecast.CombinedForecastDTO;
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
public class NotificationPayload implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long userId;
    private Long subscriptionId;
    private Boolean webOptIn;
    private CombinedForecastDTO combinedForecast;
}
