package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;

import java.util.Set;

/**
 * Serviço responsável pela orquestração e seleção de estratégias
 * de notificação conforme as preferências do usuário.
 */
public interface NotificationStrategyService {

    /**
     * Envia uma notificação pelos canais para os quais o usuário fez opt-in.
     * Busca as preferências do usuário automaticamente.
     *
     * @param payload A notificação a ser enviada
     */
    void sendNotification(NotificationPayload payload);

    /**
     * Envia uma notificação por canais específicos.
     *
     * @param payload A notificação a ser enviada
     * @param enabledChannels Os canais pelos quais a notificação deve ser enviada
     */
    void sendNotificationToEnabledChannels(NotificationPayload payload, Set<NotificationChannelsEnum> enabledChannels);

    /**
     * Envia uma notificação apenas pelo canal WebSocket.
     * Usado como fallback quando não há preferências definidas.
     *
     * @param payload A notificação a ser enviada
     */
    void sendWebNotification(NotificationPayload payload);
}
