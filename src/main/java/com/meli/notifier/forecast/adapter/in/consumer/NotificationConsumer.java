package com.meli.notifier.forecast.adapter.in.consumer;

import com.meli.notifier.forecast.config.KafkaTopicConfig;
import com.meli.notifier.forecast.domain.model.NotificationPayload;
import com.meli.notifier.forecast.application.port.in.NotificationStrategyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationStrategyService notificationStrategyService;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = KafkaTopicConfig.NOTIFICATION_TOPIC,
            concurrency = "6",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(backoff = @Backoff(delay = 2000))
    public void processNotification(NotificationPayload payload) {
        log.info("Processando notificação para usuário ID: {} via subscription ID: {}",
                payload.getUserId(), payload.getSubscriptionId());

        try {
            notificationStrategyService.sendNotification(payload);
            log.info("Notificação processada com sucesso para usuário ID: {}", payload.getUserId());
        } catch (Exception e) {
            log.error("Erro ao processar notificação para usuário ID: {}",
                    payload.getUserId(), e);
            throw e;
        }
    }

}
