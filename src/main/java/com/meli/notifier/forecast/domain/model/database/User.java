package com.meli.notifier.forecast.domain.model.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String passwordHash;
    private Boolean optIn;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
