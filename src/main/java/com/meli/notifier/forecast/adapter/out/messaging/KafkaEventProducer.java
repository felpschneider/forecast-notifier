package com.meli.notifier.forecast.adapter.out.messaging;

import com.meli.notifier.forecast.application.port.out.EventPublisherPort;
import com.meli.notifier.forecast.config.KafkaTopicConfig;
import com.meli.notifier.forecast.domain.model.NotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventProducer implements EventPublisherPort {

    private final KafkaTemplate<String, NotificationPayload> kafkaTemplate;

    @Override
    public void publishNotification(String userId, NotificationPayload payload) {
        try {
            kafkaTemplate.send(KafkaTopicConfig.NOTIFICATION_TOPIC, userId, payload)
                    .whenComplete((result, ex) -> {
                    });
            log.info("Notificação publicada com sucesso para Kafka: userId={}", userId);
        } catch (Exception e) {
            log.error("Erro ao publicar notificação: userId={}", userId, e);
        }
    }
}
