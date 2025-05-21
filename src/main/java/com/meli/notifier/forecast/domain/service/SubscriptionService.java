package com.meli.notifier.forecast.domain.service;

import com.meli.notifier.forecast.application.dto.request.SubscriptionRequestDTO;
import com.meli.notifier.forecast.domain.model.database.Subscription;
import com.meli.notifier.forecast.domain.model.database.User;

import java.util.List;
import java.util.Optional;

public interface SubscriptionService {

    Subscription createSubscription(User user, SubscriptionRequestDTO request);

    List<Subscription> findAllByActiveIsTrue();
    
    List<Subscription> findAllByUser(User user);
    
    Optional<Subscription> findById(Long id);
    
    Optional<Subscription> findByIdAndUser(Long id, User user);
    
    void deactivateSubscription(Long id, User user);
}
