package com.meli.notifier.forecast.application.service;

import com.meli.notifier.forecast.application.port.in.AuthContextService;
import com.meli.notifier.forecast.application.port.in.UserService;
import com.meli.notifier.forecast.domain.exception.AuthenticationException;
import com.meli.notifier.forecast.domain.model.database.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthContextServiceImpl implements AuthContextService {

    private final UserService userService;

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User user)) {
            throw new AuthenticationException("User not authenticated");
        }

        return userService.findById(user.getId()).orElseThrow();
    }
}
