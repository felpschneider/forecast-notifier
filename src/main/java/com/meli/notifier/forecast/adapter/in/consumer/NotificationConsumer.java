package com.meli.notifier.forecast.adapter.in.consumer;

import com.meli.notifier.forecast.application.port.in.notification.NotificationService;
import com.meli.notifier.forecast.config.KafkaTopicConfig;
import com.meli.notifier.forecast.domain.exception.NotificationException;
import com.meli.notifier.forecast.domain.model.NotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = KafkaTopicConfig.NOTIFICATION_TOPIC,
            concurrency = "6",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @RetryableTopic(backoff = @Backoff(delay = 2000))
    public void processNotification(NotificationPayload payload) throws NotificationException {
        var subscriptionId = payload.getSubscriptionId();
        log.info("Processing notifications for user ID: {} and subscription ID: {}",
                payload.getUserId(), payload.getSubscriptionId());

        try {
            notificationService.sendNotification(payload);
            log.info("Notification processed for subscription ID: {}", subscriptionId);
        } catch (Exception e) {
            log.error("Error trying to process notification for subscription ID: {}", subscriptionId, e);
            throw new NotificationException("Error trying to process notification for subscription ID: %s".formatted(subscriptionId));
        }
    }

}
