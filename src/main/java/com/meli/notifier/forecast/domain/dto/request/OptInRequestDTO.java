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
public class OptInRequestDTO {

    @NotNull(message = "The opt-in status must not be null")
    @Schema(description = "The desired opt-in status", example = "true", required = true)
    private Boolean optIn;
}
