package com.meli.notifier.forecast.application.port.in;

import com.meli.notifier.forecast.domain.model.database.NotificationChannel;
import java.util.Optional;

public interface NotificationChannelService {

    Optional<NotificationChannel> getNotificationChannelByUserId(Long userId);

    NotificationChannel saveNotificationChannel(NotificationChannel notificationChannel);
}
