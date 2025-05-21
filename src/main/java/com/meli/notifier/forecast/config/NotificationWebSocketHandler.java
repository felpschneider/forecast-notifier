package com.meli.notifier.forecast.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            userSessions.put(userId, session);
            log.info("WebSocket connection established for user: {}", userId);

            try {
                Map<String, Object> message = Map.of(
                    "type", "CONNECTED",
                    "timestamp", System.currentTimeMillis(),
                    "message", "Connected successfully"
                );
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            } catch (IOException e) {
                log.error("Error sending welcome message", e);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            if (message.getPayload().equalsIgnoreCase("ping")) {
                Map<String, Object> pongResponse = Map.of(
                    "type", "PONG",
                    "timestamp", System.currentTimeMillis()
                );
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pongResponse)));
            }
        } catch (IOException e) {
            log.error("Error handling message", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            userSessions.remove(userId);
            log.info("WebSocket connection closed for user: {}", userId);
        }
    }

    public void sendNotificationToUser(Long userId, Object notification) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String payload = objectMapper.writeValueAsString(Map.of(
                    "type", "NOTIFICATION",
                    "timestamp", System.currentTimeMillis(),
                    "data", notification
                ));
                session.sendMessage(new TextMessage(payload));
                log.debug("Notification sent to user: {}", userId);
            } catch (IOException e) {
                log.error("Error sending notification to user: {}", userId, e);
            }
        } else {
            log.warn("No active WebSocket session found for user: {}", userId);
        }
    }
}
