package com.meli.notifier.forecast.adapter.kafka.consumer;

import com.meli.notifier.forecast.config.KafkaTopicConfig;
import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import com.meli.notifier.forecast.domain.service.impl.notification.NotificationStrategyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Consumidor Kafka que processa as notificações de saída e as envia para os
 * usuários via WebSocket.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationStrategyService notificationStrategyService;

    @KafkaListener(
            groupId = "forecast-notifier-dispatchers",
            topics = KafkaTopicConfig.NOTIFICATION_TOPIC,
            concurrency = "3"
    )
    @Retryable(
            maxAttempts = 3,
            backoff = @org.springframework.retry.annotation.Backoff(delay = 2000)
    )
    public void processNotification(NotificationPayload payload) {
        log.info("Processando notificação para usuário ID: {} via subscription ID: {}",
                payload.getUserId(), payload.getSubscriptionId());

        try {
            // Por enquanto apenas web está habilitado, no futuro verificar preferências do usuário
            Set<NotificationChannelsEnum> enabledChannels = Set.of(NotificationChannelsEnum.WEB);
            
            // Enviar a notificação usando o serviço de estratégia
            notificationStrategyService.sendNotification(payload, enabledChannels);

            log.info("Notificação processada com sucesso para usuário ID: {}",
                    payload.getUserId());
        } catch (Exception e) {
            log.error("Erro ao processar notificação para usuário ID: {}",
                    payload.getUserId(), e);
            throw e; // Permite que o circuit breaker funcione
        }
    }
    
}
