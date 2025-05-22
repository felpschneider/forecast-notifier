package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.domain.dto.TokenDTO;
import com.meli.notifier.forecast.domain.dto.request.LoginRequestDTO;

public interface AuthService {

    TokenDTO login(LoginRequestDTO loginRequest);
}
