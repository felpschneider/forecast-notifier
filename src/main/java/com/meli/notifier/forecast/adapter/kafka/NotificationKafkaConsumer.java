package com.meli.notifier.forecast.adapter.kafka;

import com.meli.notifier.forecast.config.KafkaTopicConfig;
import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import com.meli.notifier.forecast.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaTopicConfig.NOTIFICATION_OUTBOUND_TOPIC, 
                  groupId = "${spring.kafka.consumer.group-id}",
                  containerFactory = "kafkaListenerContainerFactory")
    @RetryableTopic(backoff = @Backoff(delay = 2000))
    public void consume(NotificationPayload notification) {
        try {
            log.info("Received notification from Kafka for user: {}", notification.getUserId());

            notificationService.sendNotificationToUser(notification);
            
            log.debug("Notification forwarded to WebSocket service for user: {}", notification.getUserId());
        } catch (Exception e) {
            log.error("Error processing notification from Kafka", e);
            throw e;
        }
    }
}
