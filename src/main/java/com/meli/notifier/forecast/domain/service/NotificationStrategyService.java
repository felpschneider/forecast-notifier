package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.NotificationPayload;

import java.util.Set;

/**
 * Serviço responsável pela orquestração e seleção de estratégias
 * de notificação conforme as preferências do usuário.
 */
public interface NotificationStrategyService {

    void sendNotification(NotificationPayload payload);

    void sendNotificationToEnabledChannels(NotificationPayload payload, Set<NotificationChannelsEnum> enabledChannels);

    void sendWebNotification(NotificationPayload payload);
}
