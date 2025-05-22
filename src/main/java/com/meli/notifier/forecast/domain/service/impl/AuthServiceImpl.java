package com.meli.notifier.forecast.domain.service.impl;

import com.meli.notifier.forecast.domain.entity.UserEntity;
import com.meli.notifier.forecast.adapter.out.persistence.repository.UserRepository;
import com.meli.notifier.forecast.domain.dto.TokenDTO;
import com.meli.notifier.forecast.domain.dto.request.LoginRequestDTO;
import com.meli.notifier.forecast.domain.exception.AuthenticationException;
import com.meli.notifier.forecast.domain.service.AuthService;
import com.meli.notifier.forecast.domain.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TokenDTO login(LoginRequestDTO loginRequest) {
        log.debug("Attempting login for user with email: {}", loginRequest.getEmail());

        UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login attempt failed: user not found with email {}", loginRequest.getEmail());
                    return new AuthenticationException("Invalid credentials. E-mail not registered");
                });

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            log.warn("Login attempt failed: invalid password for user with email {}", loginRequest.getEmail());
            throw new AuthenticationException("Invalid credentials");
        }

        if (!user.getActive()) {
            log.warn("Login attempt failed: user account is disabled for email {}", loginRequest.getEmail());
            throw new AuthenticationException("Account is disabled");
        }

        log.info("User logged in successfully: {}", loginRequest.getEmail());

        return sessionService.createSession(user);
    }


}
