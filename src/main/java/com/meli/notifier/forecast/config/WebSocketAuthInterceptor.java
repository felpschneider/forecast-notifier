package com.meli.notifier.forecast.config;

import com.meli.notifier.forecast.adapter.persistence.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final SessionRepository sessionRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                 WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // Handle authorization from headers for standard clients
        List<String> authorization = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        
        // Check for token in URL parameters for Postman testing
        String token = null;
        String query = request.getURI().getQuery();
        
        if (query != null && query.contains("Authorization=")) {
            // Parse Authorization from URL parameters
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("Authorization=")) {
                    token = param.substring("Authorization=".length());
                    token = token.replace("Bearer+", "").replace("Bearer%20", "");
                    log.info("Found token in URL parameters: {}", token);
                    break;
                }
            }
        } else if (authorization != null && !authorization.isEmpty()) {
            // Use the standard header
            token = authorization.get(0).replace("Bearer ", "");
            log.info("Found token in Authorization header");
        } else if (query != null && query.contains("userId=")) {
            // Simple userId fallback for testing
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("userId=")) {
                    String userId = param.substring("userId=".length());
                    log.info("Using test mode with userId: {}", userId);
                    attributes.put("userId", Long.valueOf(userId));
                    return true;
                }
            }
        }
        
        if (token == null) {
            log.warn("No authentication found in request");
            return false;
        }
        
        return sessionRepository.findById(token)
                .map(session -> {
                    attributes.put("userId", session.getUser().getId());
                    log.info("WebSocket connection authorized for user: {}", session.getUser().getId());
                    return true;
                })
                .orElse(false);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                             WebSocketHandler wsHandler, Exception exception) {
        // No action needed after handshake
    }
}
