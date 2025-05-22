package com.meli.notifier.forecast.application.port.in;

import com.meli.notifier.forecast.domain.dto.TokenDTO;
import com.meli.notifier.forecast.domain.dto.request.LoginRequestDTO;

public interface AuthService {

    TokenDTO login(LoginRequestDTO loginRequest);
}
