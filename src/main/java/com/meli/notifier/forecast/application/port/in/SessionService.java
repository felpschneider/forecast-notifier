package com.meli.notifier.forecast.application.port.in;

import com.meli.notifier.forecast.domain.entity.UserEntity;
import com.meli.notifier.forecast.domain.dto.TokenDTO;
import com.meli.notifier.forecast.domain.model.database.Session;

public interface SessionService {
    TokenDTO createSession(UserEntity user);
    Session findById(String token);
}
