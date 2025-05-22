package com.meli.notifier.forecast.adapter.in.scheduler;

import com.meli.notifier.forecast.domain.event.SubscriptionEvent;
import com.meli.notifier.forecast.domain.model.database.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionListener {

    private final QuartzSchedulerService schedulerService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSubscriptionSaved(SubscriptionEvent.SubscriptionSaved event) {
        Subscription subscription = event.getSubscription();
        log.info("Subscription event received for sub id: {}", subscription.getId());
        schedulerService.scheduleOrUpdateJob(subscription);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSubscriptionDeleted(SubscriptionEvent.SubscriptionDeleted event) {
        Long subscriptionId = event.getSubscriptionId();
        log.info("Subscription delete event received for sub id {}", subscriptionId);
        schedulerService.deleteJob(subscriptionId);
    }
}
