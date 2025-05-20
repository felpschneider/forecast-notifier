package com.meli.notifier.forecast.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String NOTIFICATION_TRIGGERS_TOPIC = "notification.triggers";
    public static final String NOTIFICATION_OUTBOUND_TOPIC = "notification.outbound";

    private static final int DEFAULT_PARTITION_COUNT = 6;

    @Bean
    public NewTopic notificationTriggersTopic() {
        return TopicBuilder.name(NOTIFICATION_TRIGGERS_TOPIC)
                .partitions(DEFAULT_PARTITION_COUNT)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationOutboundTopic() {
        return TopicBuilder.name(NOTIFICATION_OUTBOUND_TOPIC)
                .partitions(DEFAULT_PARTITION_COUNT)
                .replicas(1)
                .build();
    }
}
