package com.meli.notifier.forecast.domain.model;

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
public class User {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String passwordHash;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private Set<Subscription> subscriptions = new HashSet<>();
    @Builder.Default
    private Set<Session> sessions = new HashSet<>();
    @Builder.Default
    private Set<NotificationChannel> notificationChannels = new HashSet<>();
}
