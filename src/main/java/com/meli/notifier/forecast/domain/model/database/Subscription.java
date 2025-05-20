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
public class Subscription {
    private Long id;
    private User user;
    private City city;
    private String cronExpression;
    private Boolean active;
    private LocalDateTime lastSentAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
