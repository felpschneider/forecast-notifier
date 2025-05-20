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
public class NotificationChannel {
    private Long id;
    private User user;
    private Boolean webOptIn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
