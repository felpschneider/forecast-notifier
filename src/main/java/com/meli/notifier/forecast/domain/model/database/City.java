package com.meli.notifier.forecast.domain.model.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class City {
    private Long idCptec;
    private String name;
    private String stateCode;
    private Boolean isCoastal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
