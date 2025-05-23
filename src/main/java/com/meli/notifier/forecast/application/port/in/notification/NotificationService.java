package com.meli.notifier.forecast.application.port.in.notification;

import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.NotificationPayload;

import java.util.Set;

public interface NotificationService {

    void sendNotificationToEnabledChannels(NotificationPayload payload, Set<NotificationChannelsEnum> enabledChannels);

    void sendNotification(NotificationPayload payload);
}
