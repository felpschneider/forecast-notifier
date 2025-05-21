package com.meli.notifier.forecast.domain.event;

import com.meli.notifier.forecast.domain.model.database.Subscription;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public abstract class SubscriptionEvent {

    @Getter
    @RequiredArgsConstructor
    public static class SubscriptionSaved extends SubscriptionEvent {
        private final Subscription subscription;
    }

    @Getter
    @RequiredArgsConstructor
    public static class SubscriptionDeleted extends SubscriptionEvent {
        private final Long subscriptionId;
    }
}
