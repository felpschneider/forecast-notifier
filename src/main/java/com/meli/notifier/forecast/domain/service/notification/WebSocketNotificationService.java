package com.meli.notifier.forecast.domain.service.notification;

import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotificationToUser(NotificationPayload notification) {
        String userDestination = "/queue/alerts";
        String userId = String.valueOf(notification.getUserId());
        
        log.info("Sending Websocket notification for userId: {}", userId);
        sendMessage(userId, userDestination, notification);
    }

    private void sendMessage(String userId, String userDestination, NotificationPayload notification) {
        try {
            messagingTemplate.convertAndSendToUser(userId, userDestination, notification);
            log.debug("Websocket Notification successfully sent to userId: {}", userId);
        } catch (Exception e) {
            log.error("Notification couldn't be sent to userId: {}", userId, e);
            throw e;
        }
    }
}
