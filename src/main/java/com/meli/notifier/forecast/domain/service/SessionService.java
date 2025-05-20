package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.adapter.persistence.entity.UserEntity;
import com.meli.notifier.forecast.application.dto.TokenDTO;
import com.meli.notifier.forecast.domain.model.database.Session;

public interface SessionService {
    TokenDTO createSession(UserEntity user);
    Session findById(String token);
}
