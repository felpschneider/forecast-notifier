package com.meli.notifier.forecast.domain.service.impl.notification;

import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.database.NotificationChannel;
import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import com.meli.notifier.forecast.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PushNotificationStrategyService implements NotificationService {

    // Aqui você injetaria seu serviço de Push Notification
    // private final PushNotificationService pushService;

    @Override
    public void sendNotificationToUser(NotificationPayload notification) {
        String userId = String.valueOf(notification.getUserId());

        log.info("Sending Push notification for userId: {}", userId);
        sendMessage(Long.valueOf(userId), notification);
    }

    @Override
    public NotificationChannelsEnum getChannel() {
        return NotificationChannelsEnum.PUSH;
    }

    @Override
    public boolean isEnabled(NotificationChannel preferences) {
        return Boolean.TRUE.equals(preferences.getPush_opt_in());
    }

    private void sendMessage(Long userId, NotificationPayload notification) {
        try {
            // Implementação do envio de notificação push
            // pushService.sendPushNotification(userId, createPushNotificationFromPayload(notification));
            log.debug("Push Notification successfully sent to userId: {}", userId);
        } catch (Exception e) {
            log.error("Push Notification couldn't be sent to userId: {}", userId, e);
            throw e;
        }
    }

    // Método para converter NotificationPayload para formato de notificação push
    // private PushMessage createPushNotificationFromPayload(NotificationPayload notification) {
    //    // Implementação de conversão
    // }
}
