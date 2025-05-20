package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.domain.model.database.User;

public interface AuthContextService {
    User getCurrentUser();
}
