package com.meli.notifier.forecast.application.port.in;

import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.NotificationPayload;

import java.util.Set;

public interface NotificationStrategyService {

    void sendNotification(NotificationPayload payload);

    void sendNotificationToEnabledChannels(NotificationPayload payload, Set<NotificationChannelsEnum> enabledChannels);

    void sendWebNotification(NotificationPayload payload);
}
