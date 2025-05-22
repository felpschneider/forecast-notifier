package com.meli.notifier.forecast.adapter.in.scheduler;

import com.meli.notifier.forecast.domain.model.database.Subscription;
import com.meli.notifier.forecast.domain.model.forecast.CombinedForecastDTO;
import com.meli.notifier.forecast.domain.model.NotificationPayload;
import com.meli.notifier.forecast.application.port.in.SubscriptionService;
import com.meli.notifier.forecast.application.service.CptecServiceImpl;
import com.meli.notifier.forecast.application.port.out.EventPublisherPort;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap dataMap = context.getMergedJobDataMap();
            Long subscriptionId = dataMap.getLong("subscriptionId");
            log.info("Executing notification job for subscription ID: {}", subscriptionId);
            
            Optional<Subscription> subscriptionOpt = subscriptionService.findByIdAndActiveTrue(subscriptionId);
            if (subscriptionOpt.isEmpty()) {
                throw new JobExecutionException("Subscription not found: " + subscriptionId);
            }
            
            Subscription subscription = subscriptionOpt.get();

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


