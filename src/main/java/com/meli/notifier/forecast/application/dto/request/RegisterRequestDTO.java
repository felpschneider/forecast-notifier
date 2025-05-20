package com.meli.notifier.forecast.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RegisterRequestDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
}
