package com.meli.notifier.forecast.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String NOTIFICATION_TOPIC = "notification-topic";
    private static final int DEFAULT_PARTITION_COUNT = 6;

    @Bean
    public NewTopic notificationOutboundTopic() {
        return TopicBuilder.name(NOTIFICATION_TOPIC)
                .partitions(DEFAULT_PARTITION_COUNT)
                .replicas(1)
                .build();
    }
}
