package com.meli.notifier.forecast.port.out;

import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;

public interface EventPublisherPort {

    void publishNotification(String userId, NotificationPayload payload);
}
