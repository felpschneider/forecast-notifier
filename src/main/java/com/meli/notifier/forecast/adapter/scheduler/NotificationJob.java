package com.meli.notifier.forecast.adapter.scheduler;

import com.meli.notifier.forecast.domain.model.database.Subscription;
import com.meli.notifier.forecast.domain.model.forecast.CombinedForecastDTO;
import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import com.meli.notifier.forecast.domain.service.SubscriptionService;
import com.meli.notifier.forecast.domain.service.impl.CptecServiceImpl;
import com.meli.notifier.forecast.port.out.EventPublisherPort;
import com.meli.notifier.forecast.domain.service.impl.NotificationCacheService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Quartz job for processing scheduled notifications.
 * This job checks weather conditions for subscriptions and sends notifications if needed.
 */
@Slf4j
@Component
@NoArgsConstructor
public class NotificationJob implements Job {

    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private CptecServiceImpl cptecService;
    @Autowired
    private EventPublisherPort eventPublisher;
    @Autowired
    private NotificationCacheService notificationCacheService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap dataMap = context.getMergedJobDataMap();
            Long subscriptionId = dataMap.getLong("subscriptionId");
            log.info("Executing notification job for subscription ID: {}", subscriptionId);
            
            Optional<Subscription> subscriptionOpt = subscriptionService.findById(subscriptionId);
            if (subscriptionOpt.isEmpty()) {
                throw new JobExecutionException("Subscription not found: " + subscriptionId);
            }
            
            Subscription subscription = subscriptionOpt.get();
            
            if (!subscription.getActive()) {
                log.info("Subscription {} is inactive, skipping notification check", subscriptionId);
                return;
            }
            
            Long cityId = subscription.getCity().getIdCptec();
            CombinedForecastDTO forecast = cptecService.getCombinedForecast(cityId);
            
            NotificationPayload notification = NotificationPayload.builder()
                    .userId(subscription.getUser().getId())
                    .subscriptionId(subscriptionId)
                    .combinedForecast(forecast)
                    .build();

            eventPublisher.publishNotification(
                    String.valueOf(subscription.getUser().getId()), 
                    notification);

            log.info("Notification sent for subscription {}", subscriptionId);
        } catch (Exception e) {
            log.error("Error executing notification job", e);
            throw new JobExecutionException("Failed to execute notification job", e);
        }
    }
}


