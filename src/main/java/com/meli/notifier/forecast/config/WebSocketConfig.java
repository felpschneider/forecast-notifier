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
        // Habilitar broker de mensagem simples para enviar mensagens de retorno para o cliente em destinos prefixados com /topic /queue
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Prefixo para mensagens mapeadas para métodos anotados com @MessageMapping
        registry.setApplicationDestinationPrefixes("/app");
        
        // Configuração para destinos privados de usuário
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registra o endpoint /ws-notifications com suporte para SockJS
        registry.addEndpoint("/ws-notifications")
            .setAllowedOriginPatterns("*") // Em produção, restrinja os domínios permitidos
            .withSockJS();
    }
}
