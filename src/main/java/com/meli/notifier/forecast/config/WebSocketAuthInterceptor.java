package com.meli.notifier.forecast.config;

import com.meli.notifier.forecast.adapter.out.persistence.repository.SessionRepository;
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
        List<String> authorization = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

        String token = null;

        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.getFirst().replace("Bearer ", "");
            log.info("Found token in Authorization header");
        }

        if (token == null) {
            log.warn("No authentication found in request");
            return false;
        }

        return sessionRepository.findById(token).map(session -> {
            attributes.put("userId", session.getUser().getId());
            log.info("WebSocket connection authorized for user: {}", session.getUser().getId());
            return true;
        }).orElse(false);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
