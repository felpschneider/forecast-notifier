package com.meli.notifier.forecast.application.port.in;

import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.database.NotificationChannel;
import com.meli.notifier.forecast.domain.model.NotificationPayload;

public interface NotificationService {
    void sendNotificationToUser(NotificationPayload payload);
    NotificationChannelsEnum getChannel();
    boolean isEnabled(NotificationChannel preferences);
}
