package com.meli.notifier.forecast.application.service;

import com.meli.notifier.forecast.adapter.out.persistence.repository.UserRepository;
import com.meli.notifier.forecast.application.port.in.SessionService;
import com.meli.notifier.forecast.domain.dto.TokenDTO;
import com.meli.notifier.forecast.domain.dto.request.LoginRequestDTO;
import com.meli.notifier.forecast.domain.entity.UserEntity;
import com.meli.notifier.forecast.domain.exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, sessionService, passwordEncoder);
    }

    @Test
    void givenValidCredentials_whenLogin_thenReturnToken() {
        // Arrange
        String email = "user@example.com";
        String password = "password123";
        String hashedPassword = "hashedPassword";
        var expiresAt = LocalDateTime.now().plusHours(24);
        expiresAt.format(DateTimeFormatter.ISO_DATE_TIME);
        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(email);
        userEntity.setPasswordHash(hashedPassword);
        userEntity.setActive(true);

        TokenDTO expectedToken = TokenDTO.builder()
                .token("access-token")
                .expiresAt(String.valueOf(expiresAt))
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(sessionService.createSession(userEntity)).thenReturn(expectedToken);

        // Act
        TokenDTO result = authService.login(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, hashedPassword);
        verify(sessionService).createSession(userEntity);
    }

    @Test
    void givenNonExistingEmail_whenLogin_thenThrowAuthenticationException() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "password123";

        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authService.login(loginRequest));

        assertEquals("Invalid credentials. E-mail not registered", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(sessionService, never()).createSession(any());
    }

    @Test
    void givenInvalidPassword_whenLogin_thenThrowAuthenticationException() {
        // Arrange
        String email = "user@example.com";
        String password = "wrongPassword";
        String hashedPassword = "hashedPassword";

        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(email);
        userEntity.setPasswordHash(hashedPassword);
        userEntity.setActive(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authService.login(loginRequest));

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, hashedPassword);
        verify(sessionService, never()).createSession(any());
    }

    @Test
    void givenInactiveUser_whenLogin_thenThrowAuthenticationException() {
        // Arrange
        String email = "inactive@example.com";
        String password = "password123";
        String hashedPassword = "hashedPassword";

        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(email);
        userEntity.setPasswordHash(hashedPassword);
        userEntity.setActive(false);  // Inactive user

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authService.login(loginRequest));

        assertEquals("Account is disabled", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, hashedPassword);
        verify(sessionService, never()).createSession(any());
    }
}
