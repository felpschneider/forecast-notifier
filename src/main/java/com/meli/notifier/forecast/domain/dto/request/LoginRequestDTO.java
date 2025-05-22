package com.meli.notifier.forecast.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoginRequestDTO {
    @NotNull(message = "E-mail is required")
    @Schema(description = "E-mail of your registered account", example = "person@email.com")
    private String email;

    @NotNull(message = "Password is required")
    @Schema(description = "Password of your registered account", example = "yourPassword123@")
    private String password;
}
