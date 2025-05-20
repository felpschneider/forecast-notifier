package com.meli.notifier.forecast.application.dto.request;

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
    @NotNull(message = "City ID is required")
    @Schema(description = "The ID of the city to subscribe for notifications", example = "244")
    private Long cityId;

    @NotNull(message = "Cron expression is required")
    @Schema(description = "Cron expression for the notification schedule", example = "0 0 12 * * ?")
    private String cronExpression;
}
