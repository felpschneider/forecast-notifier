package com.meli.notifier.forecast.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityResponseDTO {
    private Long idCptec;
    private String name;
    private String stateCode;
    private Boolean isCoastal;
}
