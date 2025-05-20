package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.adapter.persistence.entity.UserEntity;
import com.meli.notifier.forecast.application.dto.request.EnhancedSubscriptionRequestDTO;
import com.meli.notifier.forecast.domain.model.database.Subscription;

import java.util.List;

public interface SubscriptionService {

    Subscription createSubscription(UserEntity user, EnhancedSubscriptionRequestDTO request);

    List<Subscription> getSubscriptions(UserEntity user);

    Subscription getSubscription(UserEntity user, Long subscriptionId);

    Subscription updateSubscription(UserEntity user, Long subscriptionId, EnhancedSubscriptionRequestDTO request);

    void deleteSubscription(UserEntity user, Long subscriptionId);
}
