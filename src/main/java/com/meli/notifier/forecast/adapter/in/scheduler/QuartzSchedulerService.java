package com.meli.notifier.forecast.adapter.in.scheduler;

import com.meli.notifier.forecast.application.port.in.SubscriptionService;
import com.meli.notifier.forecast.domain.model.database.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuartzSchedulerService {

    private final Scheduler scheduler;
    private final SubscriptionService subscriptionService;

    private static final String SUBSCRIPTION_ID_KEY = "subscriptionId";
    private static final int BATCH_SIZE = 100;

    @Value("${spring.application.instance-id:${random.uuid}}")
    private String instanceId;

    @EventListener(ApplicationReadyEvent.class)
    public void scheduleAllActiveSubscriptions() {
        try {
            log.info("Starting to schedule active subscriptions on instance {}", instanceId);

            int offset = 0;
            boolean hasMoreSubscriptions = true;

            while (hasMoreSubscriptions) {
                List<Subscription> batch = subscriptionService.findActiveSubscriptionsWithPagination(offset, BATCH_SIZE);
                log.info("Processing batch of {} active subscriptions, offset {}", batch.size(), offset);

                for (Subscription subscription : batch) {
                    scheduleOrUpdateJob(subscription);
                }

                offset += batch.size();

                hasMoreSubscriptions = !batch.isEmpty() && batch.size() == BATCH_SIZE;
            }

            log.info("Initial subscription scheduling completed on instance {}", instanceId);
        } catch (Exception e) {
            log.error("Error scheduling active subscriptions during startup", e);
        }
    }

    public void scheduleOrUpdateJob(Subscription subscription) {
        try {
            if (!subscription.getActive()) {
                deleteJob(subscription.getId());
                log.info("Subscription {} is inactive, job removed", subscription.getId());
                return;
            }

            JobDetail jobDetail = buildJobDetail(subscription);
            CronTrigger trigger = buildCronTrigger(subscription);

            if (scheduler.checkExists(jobDetail.getKey())) {
                scheduler.rescheduleJob(trigger.getKey(), trigger);
                log.info("Existing job updated for subscription ID: {}", subscription.getId());
                return;
            }

            scheduler.scheduleJob(jobDetail, trigger);
            log.info("New job scheduled for subscription ID: {}", subscription.getId());
        } catch (SchedulerException e) {
            log.error("Error scheduling job for subscription ID: {}", subscription.getId(), e);
        }
    }

    public void deleteJob(Long subscriptionId) {
        try {
            JobKey jobKey = new JobKey("sub-" + subscriptionId);
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                log.info("Job removed for subscription ID: {}", subscriptionId);
            }
        } catch (SchedulerException e) {
            log.error("Error removing job for subscription ID: {}", subscriptionId, e);
        }
    }

    private JobDetail buildJobDetail(Subscription subscription) {
        return JobBuilder.newJob(NotificationJob.class)
                .withIdentity("sub-" + subscription.getId())
                .usingJobData(SUBSCRIPTION_ID_KEY, subscription.getId())
//                .storeDurably() todo remove this line
                .build();
    }

    private CronTrigger buildCronTrigger(Subscription subscription) {
        return TriggerBuilder.newTrigger()
                .withIdentity("sub-" + subscription.getId())
                .withSchedule(CronScheduleBuilder.cronSchedule(subscription.getCronExpression())
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();
    }
}
