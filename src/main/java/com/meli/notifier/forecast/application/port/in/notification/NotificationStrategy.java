package com.meli.notifier.forecast.application.port.in.notification;

import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.NotificationPayload;
import com.meli.notifier.forecast.domain.model.database.NotificationChannel;

public interface NotificationStrategy {
    void sendNotificationToUser(NotificationPayload payload);

    NotificationChannelsEnum getChannel();

    boolean isEnabled(NotificationChannel preferences);
}
