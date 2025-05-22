package com.meli.notifier.forecast.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CityRequestDTO {
    @NotNull(message = "City ID is required")
    @Schema(description = "The ID of the city to subscribe for notifications", example = "241")
    private Long idCptec;

    @NotNull(message = "City name is required")
    @Schema(description = "The name of the city to subscribe for notifications", example = "Rio de Janeiro")
    private String name;
}
