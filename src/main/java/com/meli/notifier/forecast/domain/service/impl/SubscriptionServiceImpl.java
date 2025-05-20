package com.meli.notifier.forecast.domain.service.impl;

import com.meli.notifier.forecast.adapter.persistence.entity.CityEntity;
import com.meli.notifier.forecast.adapter.persistence.entity.SubscriptionEntity;
import com.meli.notifier.forecast.adapter.persistence.repository.CityRepository;
import com.meli.notifier.forecast.adapter.persistence.repository.SubscriptionRepository;
import com.meli.notifier.forecast.application.dto.request.SubscriptionRequestDTO;
import com.meli.notifier.forecast.domain.exception.NotFoundException;
import com.meli.notifier.forecast.domain.exception.ValidationException;
import com.meli.notifier.forecast.domain.mapper.SubscriptionMapper;
import com.meli.notifier.forecast.domain.mapper.UserMapper;
import com.meli.notifier.forecast.domain.model.database.Subscription;
import com.meli.notifier.forecast.domain.model.database.User;
import com.meli.notifier.forecast.domain.service.CityService;
import com.meli.notifier.forecast.domain.service.CptecService;
import com.meli.notifier.forecast.domain.service.CronService;
import com.meli.notifier.forecast.domain.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CityRepository cityRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final CronService cronService;
    private final UserMapper userMapper;
    private final CityService cityService;
    private final CptecService cptecService;

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

        // Schedule the job using the cron expression


        log.info("Subscription created successfully with id: {}", savedEntity.getId());
        return subscriptionMapper.toModel(savedEntity);
    }

}
