package com.meli.notifier.forecast.domain.model.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class City implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long idCptec;
    private String name;
    private String stateCode;
    private Boolean isCoastal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
