package com.meli.notifier.forecast.application.controller.websocket;

import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import com.meli.notifier.forecast.domain.service.impl.notification.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final WebSocketNotificationService webSocketNotificationService;

    @MessageMapping("/notifications/send")
    public void receiveNotification(@Payload NotificationPayload payload) {
        log.info("Received notification payload: {}", payload);
        webSocketNotificationService.sendNotificationToUser(payload);
    }

    @MessageMapping("/notifications/ping")
    @SendToUser("/queue/notifications/pong")
    public Map<String, Object> handlePing(@Payload(required = false) String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", "ok");
        return response;
    }
}
