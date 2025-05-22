package com.meli.notifier.forecast.application.port.in;

import com.meli.notifier.forecast.domain.model.database.Subscription;
import com.meli.notifier.forecast.domain.model.database.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SubscriptionService {

    Subscription createSubscription(User user, Subscription request);

    List<Subscription> findAllByActiveIsTrue();

    List<Subscription> findActiveSubscriptionsWithPagination(int offset, int limit);

    List<Subscription> findAllByUser(User user);

    Optional<Subscription> findById(Long id);

    @Transactional(readOnly = true)
    Optional<Subscription> findByIdAndActiveTrue(Long id);

    Optional<Subscription> findByIdAndUser(Long id, User user);

    void deactivateSubscription(Long id, User user);

    Optional<Subscription> findByUserIdAndCityId(Long id, Long idCptec);
}
