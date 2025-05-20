package com.meli.notifier.forecast.port.out;

import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;

/**
 * Porta de saída para publicação de notificações para os usuários.
 * 
 * Define o contrato para publicar eventos de notificação para
 * entrega via WebSocket ou outros canais.
 */
public interface EventPublisherPort {

    /**
     * Publica uma notificação para um usuário específico.
     * 
     * @param userId ID do usuário destinatário
     * @param payload Payload da notificação
     */
    void publishNotification(String userId, NotificationPayload payload);
}
