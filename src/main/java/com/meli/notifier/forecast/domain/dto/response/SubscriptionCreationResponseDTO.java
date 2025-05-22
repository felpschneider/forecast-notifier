package com.meli.notifier.forecast.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionCreationResponseDTO {
    private Long id;
    private UserResponseDTO user;
    private CityResponseDTO city;
    private String cronExpression;
}
