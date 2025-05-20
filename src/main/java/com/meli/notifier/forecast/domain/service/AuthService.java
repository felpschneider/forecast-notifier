package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.application.dto.TokenDTO;
import com.meli.notifier.forecast.application.dto.request.LoginRequestDTO;

public interface AuthService {

    TokenDTO login(LoginRequestDTO loginRequest);
}
