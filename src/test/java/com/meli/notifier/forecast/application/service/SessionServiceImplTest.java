package com.meli.notifier.forecast.application.service;

import com.meli.notifier.forecast.adapter.out.persistence.repository.SessionRepository;
import com.meli.notifier.forecast.domain.dto.TokenDTO;
import com.meli.notifier.forecast.domain.entity.SessionEntity;
import com.meli.notifier.forecast.domain.entity.UserEntity;
import com.meli.notifier.forecast.domain.mapper.SessionMapper;
import com.meli.notifier.forecast.domain.model.database.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private SessionMapper sessionMapper;

    private SessionServiceImpl sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionServiceImpl(sessionRepository, sessionMapper);
    }

    @Test
    void givenValidUserEntity_whenCreateSession_thenReturnTokenDTO() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@example.com");

        // Act
        TokenDTO result = sessionService.createSession(user);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertNotNull(result.getExpiresAt());

        ArgumentCaptor<SessionEntity> sessionCaptor = ArgumentCaptor.forClass(SessionEntity.class);
        verify(sessionRepository).save(sessionCaptor.capture());

        SessionEntity savedSession = sessionCaptor.getValue();
        assertEquals(result.getToken(), savedSession.getId());
        assertEquals(user, savedSession.getUser());
        assertNotNull(savedSession.getExpiresAt());
    }

    @Test
    void givenValidToken_whenFindById_thenReturnSession() {
        // Arrange
        String token = "valid-token";
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setId(token);

        Session expectedSession = Session.builder()
                .id(token)
                .build();

        when(sessionRepository.findById(token)).thenReturn(Optional.of(sessionEntity));
        when(sessionMapper.toModel(sessionEntity)).thenReturn(expectedSession);

        // Act
        Session result = sessionService.findById(token);

        // Assert
        assertNotNull(result);
        assertEquals(token, result.getId());
        verify(sessionRepository).findById(token);
        verify(sessionMapper).toModel(sessionEntity);
    }

    @Test
    void givenInvalidToken_whenFindById_thenReturnNull() {
        // Arrange
        String token = "invalid-token";
        when(sessionRepository.findById(token)).thenReturn(Optional.empty());

        // Act
        Session result = sessionService.findById(token);

        // Assert
        assertNull(result);
        verify(sessionRepository).findById(token);
        verifyNoInteractions(sessionMapper);
    }
}
