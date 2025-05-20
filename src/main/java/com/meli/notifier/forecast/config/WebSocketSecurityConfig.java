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
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.Optional;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
@Slf4j
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final SessionRepository sessionRepository;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = extractToken(accessor);
                    
                    if (StringUtils.hasText(token)) {
                        // Valida o token e configura a autenticação
                        setPrincipalIfValid(token, accessor);
                    } else {
                        log.warn("Tentativa de conexão WebSocket sem token de autenticação");
                    }
                }
                return message;
            }
        });
    }
    
    private String extractToken(StompHeaderAccessor accessor) {
        // O token é enviado como um cabeçalho "Authorization" no formato "Bearer {token}"
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
            log.debug("WebSocket autenticado com sucesso para o usuário: {}", principal.getName());
        } else {
            log.warn("Token inválido para conexão WebSocket: {}", token);
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
