package com.meli.notifier.forecast.application.port.out;

import com.meli.notifier.forecast.domain.model.NotificationPayload;

public interface EventPublisherPort {

    void publishNotification(String userId, NotificationPayload payload);
}
