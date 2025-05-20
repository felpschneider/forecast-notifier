package com.meli.notifier.forecast.adapter.scheduler;

import com.meli.notifier.forecast.adapter.persistence.entity.SubscriptionEntity;
import com.meli.notifier.forecast.domain.event.SubscriptionEvent;
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
        SubscriptionEntity subscription = event.getSubscription();
        log.info("Recebido evento de subscrição salva para o ID: {}", subscription.getId());
        schedulerService.scheduleOrUpdateJob(subscription);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSubscriptionDeleted(SubscriptionEvent.SubscriptionDeleted event) {
        Long subscriptionId = event.getSubscriptionId();
        log.info("Recebido evento de subscrição excluída para o ID: {}", subscriptionId);
        schedulerService.deleteJob(subscriptionId);
    }
}
