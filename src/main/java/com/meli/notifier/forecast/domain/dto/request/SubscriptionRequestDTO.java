package com.meli.notifier.forecast.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubscriptionRequestDTO {
    @NotNull(message = "City is required")
    @Schema(description = "City information for the subscription", example = "Rio de Janeiro")
    private CityRequestDTO city;

    @NotNull(message = "Cron expression is required")
    @Schema(description = "Cron expression for the notification schedule", example = "0 * * * * ?")
    private String cronExpression;
}
