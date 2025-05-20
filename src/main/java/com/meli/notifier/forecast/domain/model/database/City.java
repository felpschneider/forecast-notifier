package com.meli.notifier.forecast.domain.model.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @Builder.Default
    private Set<Subscription> subscriptions = new HashSet<>();
}
