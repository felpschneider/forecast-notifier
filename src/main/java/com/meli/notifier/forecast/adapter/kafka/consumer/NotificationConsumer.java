package com.meli.notifier.forecast.adapter.kafka.consumer;

import com.meli.notifier.forecast.config.KafkaTopicConfig;
import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import com.meli.notifier.forecast.domain.service.impl.notification.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor Kafka que processa as notificações de saída e as envia para os
 * usuários via WebSocket.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final WebSocketNotificationService webSocketNotificationService;

    /**
     * Recebe notificações do tópico notification.outbound e as envia para os
     * usuários via WebSocket.
     *
     * @param payload A notificação a ser enviada
     */
    @KafkaListener(
            groupId = "forecast-notifier-dispatchers",
            topics = KafkaTopicConfig.NOTIFICATION_OUTBOUND_TOPIC,
            concurrency = "3"
    )
    public void processNotification(NotificationPayload payload) {
        log.info("Processando notificação para usuário ID: {} via subscription ID: {}",
                payload.getUserId(), payload.getSubscriptionId());

        try {
            // Enviar a notificação via WebSocket
            webSocketNotificationService.sendNotificationToUser(payload);

            log.info("Notificação entregue com sucesso para usuário ID: {}",
                    payload.getUserId());
        } catch (Exception e) {
            log.error("Erro ao enviar notificação via WebSocket para usuário ID: {}",
                    payload.getUserId(), e);
            // Considerar um mecanismo de retry ou fallback para notificações que falham
        }
    }
}
