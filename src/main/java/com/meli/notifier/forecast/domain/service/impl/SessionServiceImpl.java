package com.meli.notifier.forecast.domain.service.impl;

import com.meli.notifier.forecast.domain.entity.SessionEntity;
import com.meli.notifier.forecast.domain.entity.UserEntity;
import com.meli.notifier.forecast.adapter.out.persistence.repository.SessionRepository;
import com.meli.notifier.forecast.domain.dto.TokenDTO;
import com.meli.notifier.forecast.domain.mapper.SessionMapper;
import com.meli.notifier.forecast.domain.model.database.Session;
import com.meli.notifier.forecast.domain.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    private static final int SESSION_VALIDITY_HOURS = 24;

    @Override
    @Transactional
    public TokenDTO createSession(UserEntity user) {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(SESSION_VALIDITY_HOURS);
        String token = UUID.randomUUID().toString();

        SessionEntity session = SessionEntity.builder() // todo: use mapper instead of builder
                .id(token)
                .user(user)
                .expiresAt(expiresAt)
                .build();

        sessionRepository.save(session);
        log.debug("Session created for user ID: {} with expiration: {}", user.getId(), expiresAt);

        return TokenDTO.builder()
                .token(token)
                .expiresAt(expiresAt.format(DateTimeFormatter.ISO_DATE_TIME))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Session findById(String token) {
        log.debug("Finding session by id: {}", token);
        return sessionRepository.findById(token)
                .map(sessionMapper::toModel)
                .orElse(null);
    }
}
