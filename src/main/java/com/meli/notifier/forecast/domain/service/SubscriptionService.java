package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.application.dto.request.SubscriptionRequestDTO;
import com.meli.notifier.forecast.domain.model.database.Subscription;
import com.meli.notifier.forecast.domain.model.database.User;

import java.util.List;

public interface SubscriptionService {

    Subscription createSubscription(User user, SubscriptionRequestDTO request);

    List<Subscription> findAllByActiveIsTrue();
}
