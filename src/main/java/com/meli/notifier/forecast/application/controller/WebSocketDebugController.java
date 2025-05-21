package com.meli.notifier.forecast.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug/websocket")
@RequiredArgsConstructor
@Slf4j
public class WebSocketDebugController {

    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("websocketActive", true);
        status.put("brokerDestinations", new String[]{"/topic", "/queue", "/user"});
        status.put("timestamp", System.currentTimeMillis());

        log.info("WebSocket debug status checked");
        return ResponseEntity.ok(status);
    }

    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, Object>> broadcastMessage(@RequestBody String message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "BROADCAST");
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());

        // Envia para um tópico geral que todos podem se inscrever
        messagingTemplate.convertAndSend("/topic/broadcast", notification);

        log.info("Broadcast message sent to /topic/broadcast: {}", message);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "sent");
        response.put("destination", "/topic/broadcast");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send/{userId}")
    public ResponseEntity<Map<String, Object>> sendToUser(
            @PathVariable String userId,
            @RequestBody String message) {

        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "TEST");
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());

        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications/alerts", notification);

        log.info("Test message sent to user {} at /user/queue/notifications/alerts: {}", userId, message);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "sent");
        response.put("userId", userId);
        response.put("destination", "/user/queue/notifications/alerts");
        return ResponseEntity.ok(response);
    }
}
