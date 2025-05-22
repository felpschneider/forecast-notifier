package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.database.NotificationChannel;
import com.meli.notifier.forecast.domain.model.NotificationPayload;

public interface NotificationService {
    void sendNotificationToUser(NotificationPayload payload);
    NotificationChannelsEnum getChannel();

    /**
     * Verifica se o canal de notificação está habilitado para o usuário
     * @param preferences Preferências de notificação do usuário
     * @return true se o canal está habilitado, false caso contrário
     */
    boolean isEnabled(NotificationChannel preferences);
}
