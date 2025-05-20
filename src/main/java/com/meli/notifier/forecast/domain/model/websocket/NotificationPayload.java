package com.meli.notifier.forecast.domain.model.websocket;

import com.meli.notifier.forecast.domain.model.forecast.CombinedForecastDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPayload {
    private Long userId;
    private Long subscriptionId;
    private CombinedForecastDTO combinedForecast;
}
