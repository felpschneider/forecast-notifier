package com.meli.notifier.forecast.application.service.notification;

import com.meli.notifier.forecast.application.port.in.notification.NotificationStrategy;
import com.meli.notifier.forecast.config.NotificationWebSocketHandler;
import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.NotificationPayload;
import com.meli.notifier.forecast.domain.model.database.NotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketNotificationStrategyService implements NotificationStrategy {

    private final NotificationWebSocketHandler webSocketHandler;

    @Override
    public void sendNotificationToUser(NotificationPayload notification) {
        String userId = String.valueOf(notification.getUserId());

        log.info("Sending Websocket notification for userId: {}", userId);
        sendMessage(Long.valueOf(userId), notification);
    }

    @Override
    public NotificationChannelsEnum getChannel() {
        return NotificationChannelsEnum.WEB;
    }

    @Override
    public boolean isEnabled(NotificationChannel preferences) {
        return Boolean.TRUE.equals(preferences.getWebOptIn());
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
