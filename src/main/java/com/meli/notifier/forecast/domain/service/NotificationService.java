package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;

public interface NotificationService {
    void sendNotificationToUser(NotificationPayload payload);
}
