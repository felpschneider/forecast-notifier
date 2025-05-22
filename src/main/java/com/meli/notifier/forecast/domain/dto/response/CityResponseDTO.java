package com.meli.notifier.forecast.domain.dto.response;

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
