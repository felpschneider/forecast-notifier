package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.domain.model.database.User;

public interface UserService {
    void createUser(User user);

    User findById(Long id);
}
