package com.meli.notifier.forecast.domain.service.impl.notification;

import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import com.meli.notifier.forecast.domain.service.NotificationService;
import com.meli.notifier.forecast.domain.service.impl.NotificationCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketNotificationService implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationCacheService notificationCacheService;

    @Override
    public void sendNotificationToUser(NotificationPayload notification) {
        String userDestination = "/queue/notifications/alerts";
        String userId = String.valueOf(notification.getUserId());
        
        notificationCacheService.cachePendingNotification(userId, notification);

        log.info("Sending Websocket notification for userId: {}, to destination: {}", userId, userDestination);
        sendMessage(userId, userDestination, notification);

        notificationCacheService.markAsDelivered(userId, notification);
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
