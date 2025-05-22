package com.meli.notifier.forecast.application.port.in;

import com.meli.notifier.forecast.domain.model.database.User;

public interface AuthContextService {
    User getCurrentUser();
}
