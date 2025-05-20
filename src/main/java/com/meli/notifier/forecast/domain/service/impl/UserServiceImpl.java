package com.meli.notifier.forecast.domain.service.impl;

import com.meli.notifier.forecast.adapter.persistence.entity.UserEntity;
import com.meli.notifier.forecast.adapter.persistence.repository.UserRepository;
import com.meli.notifier.forecast.domain.mapper.UserMapper;
import com.meli.notifier.forecast.domain.model.database.User;
import com.meli.notifier.forecast.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void createUser(User user) {
        log.debug("Creating new user with email: {}", user.getEmail());

        String passwordHash = passwordEncoder.encode(user.getPasswordHash());

        user = user.toBuilder()
                .passwordHash(passwordHash)
                .optIn(true)
                .active(true)
                .build();

        UserEntity savedUser = userRepository.save(userMapper.toEntity(user));
        log.info("User created successfully with ID: {}", savedUser.getId());
    }

    @Override
    public Optional<User> findById(Long id) {
        log.debug("Finding user by ID: {}", id);
        return userRepository.findById(id).map(userMapper::toModel);
    }
}
