package com.meli.notifier.forecast.domain.service.impl;

import com.meli.notifier.forecast.adapter.out.persistence.entity.CityEntity;
import com.meli.notifier.forecast.adapter.out.persistence.entity.SubscriptionEntity;
import com.meli.notifier.forecast.adapter.out.persistence.repository.CityRepository;
import com.meli.notifier.forecast.adapter.out.persistence.repository.SubscriptionRepository;
import com.meli.notifier.forecast.application.dto.request.SubscriptionRequestDTO;
import com.meli.notifier.forecast.domain.event.SubscriptionEvent;
import com.meli.notifier.forecast.domain.exception.NotFoundException;
import com.meli.notifier.forecast.domain.exception.ValidationException;
import com.meli.notifier.forecast.domain.mapper.SubscriptionMapper;
import com.meli.notifier.forecast.domain.mapper.UserMapper;
import com.meli.notifier.forecast.domain.model.database.Subscription;
import com.meli.notifier.forecast.domain.model.database.User;
import com.meli.notifier.forecast.domain.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CityRepository cityRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public Subscription createSubscription(User user, SubscriptionRequestDTO request) {
        log.info("Creating subscription for user id: {} and city id: {}", user.getId(), request.getCityId());
        if (request == null || request.getCityId() == null) {
            throw new ValidationException("City ID is required");
        }
        CityEntity city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new NotFoundException("City not found with id: " + request.getCityId()));
        Optional<SubscriptionEntity> existingSubscription = subscriptionRepository.findByUserIdAndCityId(user.getId(), city.getIdCptec());
        if (existingSubscription.isPresent()) {
            throw new ValidationException("Subscription already exists for this city");
        }

        SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
                .user(userMapper.toEntity(user))
                .city(city)
                .cronExpression(request.getCronExpression())
                .active(true)
                .build();
                
        SubscriptionEntity savedEntity = subscriptionRepository.save(subscriptionEntity);
        Subscription sub = subscriptionMapper.toModel(savedEntity);
        eventPublisher.publishEvent(new SubscriptionEvent.SubscriptionSaved(sub));
        log.info("Subscription created successfully with id: {}", savedEntity.getId());
        return sub;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subscription> findAllByActiveIsTrue() {
        log.info("Fetching all active subscriptions");
        return subscriptionRepository.findAllByActiveIsTrue().stream()
                .map(subscriptionMapper::toModel)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subscription> findAllByUser(User user) {
        log.info("Fetching all subscriptions for user id: {}", user.getId());
        return subscriptionRepository.findAllByUserId(user.getId()).stream()
                .map(subscriptionMapper::toModel)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Subscription> findById(Long id) {
        log.debug("Finding subscription by id: {}", id);
        return subscriptionRepository.findById(id)
                .map(subscriptionMapper::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Subscription> findByIdAndUser(Long id, User user) {
        log.info("Finding subscription by id: {} for user id: {}", id, user.getId());
        return subscriptionRepository.findByIdAndUserId(id, user.getId())
                .map(subscriptionMapper::toModel);
    }

    @Override
    @Transactional
    public void deactivateSubscription(Long id, User user) {
        log.info("Deactivating subscription id: {} for user id: {}", id, user.getId());
        
        SubscriptionEntity subscription = subscriptionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Subscription not found with id: " + id));

        subscription.setActive(false);
        subscriptionRepository.save(subscription);
        
        // Publish event for further processing
        eventPublisher.publishEvent(new SubscriptionEvent.SubscriptionDeleted(id));
        
        log.info("Subscription successfully deactivated with id: {}", id);
    }
}
