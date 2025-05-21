package com.meli.notifier.forecast.config;

import com.meli.notifier.forecast.adapter.out.persistence.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.Optional;

/**
 * Security configuration for WebSocket STOMP connections.
 * Validates tokens sent in STOMP headers and sets principal for authenticated users.
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
@Slf4j
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final SessionRepository sessionRepository;    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    log.info("WebSocket STOMP connection attempt received");
                    String token = extractToken(accessor);

                    if (StringUtils.hasText(token)) {
                        log.debug("Token found, attempting authentication");
                        setPrincipalIfValid(token, accessor);
                    } else {
                        log.warn("No authentication token found in WebSocket connection");
                    }
                }
                return message;
            }
        });
    }    private String extractToken(StompHeaderAccessor accessor) {
        // Token is sent in the Authorization header in "Bearer {token}" format
        String authorization = accessor.getFirstNativeHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        return null;
    }

    private void setPrincipalIfValid(String token, StompHeaderAccessor accessor) {
        Optional<Long> userIdOpt = sessionRepository.findById(token)
                .map(session -> session.getUser().getId());

        if (userIdOpt.isPresent()) {
            Principal principal = new TokenPrincipal(userIdOpt.get().toString());
            accessor.setUser(principal);
            log.debug("WebSocket authentication successful for user: {}", principal.getName());
        } else {
            log.warn("Invalid token for WebSocket connection: {}", token);
        }
    }

    // Simple Principal implementation
    private static class TokenPrincipal implements Principal {
        private final String name;

        public TokenPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
