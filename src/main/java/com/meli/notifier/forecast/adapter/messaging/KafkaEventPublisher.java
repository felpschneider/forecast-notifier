package com.meli.notifier.forecast.adapter.messaging;

import com.meli.notifier.forecast.config.KafkaTopicConfig;
import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import com.meli.notifier.forecast.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Implementação do EventPublisherPort que utiliza Kafka e WebSocket para
 * publicar notificações.
 * 
 * Publica as notificações no tópico Kafka notification.outbound e
 * diretamente para o WebSocket.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisherPort {

    private final KafkaTemplate<String, NotificationPayload> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void publishNotification(String userId, NotificationPayload payload) {
        try {
            // Publicar no tópico Kafka para processamento por outros consumidores
            kafkaTemplate.send(KafkaTopicConfig.NOTIFICATION_OUTBOUND_TOPIC, userId, payload)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.debug("Notificação enviada para Kafka: userId={}, offset={}", 
                                    userId, result.getRecordMetadata().offset());
                        } else {
                            log.error("Erro ao enviar notificação para Kafka: userId={}", userId, ex);
                        }
                    });
            
            // Publicar diretamente para o WebSocket do usuário específico
            String destination = "/user/" + userId + "/queue/notifications";
            messagingTemplate.convertAndSend(destination, payload);
            
            log.info("Notificação publicada com sucesso: userId={}", userId);
        } catch (Exception e) {
            log.error("Erro ao publicar notificação: userId={}", userId, e);
        }
    }
}
