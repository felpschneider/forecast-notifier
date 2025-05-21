package com.meli.notifier.forecast.domain.service.impl.notification;

import com.meli.notifier.forecast.config.NotificationWebSocketHandler;
import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import com.meli.notifier.forecast.domain.service.NotificationService;
import com.meli.notifier.forecast.domain.service.impl.NotificationCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketNotificationStrategyService implements NotificationService {

    private final NotificationWebSocketHandler webSocketHandler;
    private final NotificationCacheService notificationCacheService;

    @Override
    public void sendNotificationToUser(NotificationPayload notification) {
        String userId = String.valueOf(notification.getUserId());

        log.info("Sending Websocket notification for userId: {}", userId);
        sendMessage(Long.valueOf(userId), notification);
    }

    private void sendMessage(Long userId, NotificationPayload notification) {
        try {
            webSocketHandler.sendNotificationToUser(userId, notification);
            log.debug("Websocket Notification successfully sent to userId: {}", userId);
        } catch (Exception e) {
            log.error("Notification couldn't be sent to userId: {}", userId, e);
            throw e;
        }
    }
}
