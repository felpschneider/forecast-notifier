package com.meli.notifier.forecast.application.service;

import com.meli.notifier.forecast.adapter.out.persistence.repository.UserRepository;
import com.meli.notifier.forecast.domain.entity.UserEntity;
import com.meli.notifier.forecast.domain.exception.NotFoundException;
import com.meli.notifier.forecast.domain.mapper.UserMapper;
import com.meli.notifier.forecast.domain.model.database.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, userMapper);
    }

    @Test
    void givenValidUser_whenCreateUser_thenUserIsSaved() {
        // Arrange
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        User user = User.builder()
                .email("user@example.com")
                .passwordHash(rawPassword)
                .name("Test User")
                .build();

        User expectedUser = user.toBuilder()
                .passwordHash(encodedPassword)
                .optIn(true)
                .active(true)
                .build();

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userMapper.toEntity(any(User.class))).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        // Act
        userService.createUser(user);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).toEntity(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertEquals(encodedPassword, capturedUser.getPasswordHash());
        assertTrue(capturedUser.getOptIn());
        assertTrue(capturedUser.getActive());

        verify(userRepository).save(userEntity);
    }

    @Test
    void givenUserId_whenFindById_thenReturnUser() {
        // Arrange
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail("user@example.com");

        User expectedUser = User.builder()
                .id(userId)
                .email("user@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toModel(userEntity)).thenReturn(expectedUser);

        // Act
        Optional<User> result = userService.findById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
        verify(userRepository).findById(userId);
        verify(userMapper).toModel(userEntity);
    }

    @Test
    void givenNonExistingUserId_whenFindById_thenReturnEmptyOptional() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findById(userId);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findById(userId);
        verify(userMapper, never()).toModel(any(UserEntity.class));
    }

    @Test
    void givenExistingUserAndOptInStatus_whenSetOptInStatus_thenUserIsUpdatedAndStatusReturned() {
        // Arrange
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("user@example.com")
                .build();

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail("user@example.com");
        userEntity.setOptIn(false);

        UserEntity updatedUserEntity = new UserEntity();
        updatedUserEntity.setId(userId);
        updatedUserEntity.setEmail("user@example.com");
        updatedUserEntity.setOptIn(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(updatedUserEntity);

        // Act
        Boolean result = userService.setOptInStatus(user, true);

        // Assert
        assertTrue(result);
        verify(userRepository).findById(userId);
        verify(userRepository).save(userEntity);
        assertEquals(true, userEntity.getOptIn());
    }

    @Test
    void givenNonExistingUser_whenSetOptInStatus_thenThrowNotFoundException() {
        // Arrange
        Long userId = 999L;
        User user = User.builder()
                .id(userId)
                .email("user@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.setOptInStatus(user, true));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }
}
