package com.meli.notifier.forecast.application.controller.websocket;

import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import com.meli.notifier.forecast.domain.service.impl.notification.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final WebSocketNotificationService webSocketNotificationService;

    @MessageMapping("/send")
    @SendTo("/topic/notifications")
    public void receiveNotification(@Payload NotificationPayload payload, Principal principal) {
        log.info("Received notification payload from user {}: {}", principal.getName(), payload);
        webSocketNotificationService.sendNotificationToUser(payload);
    }

    @SubscribeMapping("/queue/notifications/alerts")
    public Map<String, Object> subscribeToNotifications(SimpMessageHeaderAccessor headerAccessor) {
        Principal user = headerAccessor.getUser();
        if (user != null) {
            String userId = user.getName();
            log.info("User {} subscribed to notifications alerts", userId);

            Map<String, Object> response = new HashMap<>();
            response.put("type", "SUBSCRIPTION_ACK");
            response.put("message", "Successfully subscribed to notifications");
            response.put("timestamp", System.currentTimeMillis());
            return response;
        }
        return null;
    }
}
