package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.domain.model.database.User;

import java.util.Optional;

public interface UserService {
    void createUser(User user);

    Optional<User> findById(Long id);
}
