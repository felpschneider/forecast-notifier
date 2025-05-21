package com.meli.notifier.forecast.config;

import com.meli.notifier.forecast.adapter.persistence.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
@Slf4j
public class WebSocketChannelInterceptor implements WebSocketMessageBrokerConfigurer {

    private final SessionRepository sessionRepository;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    log.info("WebSocket connection attempt received");
                    String token = extractToken(accessor);

                    if (StringUtils.hasText(token)) {
                        log.debug("Token found, attempting authentication");
                        setPrincipalIfValid(token, accessor);
                    }
                }
                return message;
            }
        });
    }

    private String extractToken(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader("Authorization");

        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
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
