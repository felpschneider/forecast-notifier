package com.meli.notifier.forecast.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Definindo destinos de mensagens de broadcast e filas
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Prefixo para mensagens mapeadas para métodos anotados com @MessageMapping
        registry.setApplicationDestinationPrefixes("/app");
        
        // Configuração para destinos privados de usuário
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registra o endpoint principal de conexão WebSocket
        registry.addEndpoint("/notifications")
            .setAllowedOriginPatterns("*") // Em produção, restrinja os domínios permitidos
            .withSockJS();
    }
}
