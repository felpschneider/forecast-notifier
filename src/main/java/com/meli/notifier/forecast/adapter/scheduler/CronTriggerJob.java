package com.meli.notifier.forecast.adapter.scheduler;

import com.meli.notifier.forecast.config.KafkaTopicConfig;
import com.meli.notifier.forecast.domain.event.TriggerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CronTriggerJob implements Job {

    private final KafkaTemplate<String, TriggerEvent> kafkaTemplate;

    private static final String SUBSCRIPTION_ID_KEY = "subscriptionId";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap dataMap = context.getMergedJobDataMap();

            Long subscriptionId = dataMap.getLong(SUBSCRIPTION_ID_KEY);
            log.debug("Executando job para a subscription ID: {}", subscriptionId);

            TriggerEvent event = TriggerEvent.create(subscriptionId);

            kafkaTemplate.send(KafkaTopicConfig.NOTIFICATION_TRIGGERS_TOPIC, subscriptionId.toString(), event);

            log.info("Evento de trigger publicado para subscription ID: {}", subscriptionId);
        } catch (Exception e) {
            log.error("Erro ao executar CronTriggerJob", e);
            throw new JobExecutionException("Falha ao executar job", e);
        }
    }
}

