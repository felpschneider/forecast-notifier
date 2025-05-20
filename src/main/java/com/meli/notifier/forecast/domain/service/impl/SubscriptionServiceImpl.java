package com.meli.notifier.forecast.domain.service.impl;

import com.meli.notifier.forecast.adapter.persistence.entity.CityEntity;
import com.meli.notifier.forecast.adapter.persistence.entity.SubscriptionEntity;
import com.meli.notifier.forecast.adapter.persistence.entity.UserEntity;
import com.meli.notifier.forecast.adapter.persistence.repository.CityRepository;
import com.meli.notifier.forecast.adapter.persistence.repository.SubscriptionRepository;
import com.meli.notifier.forecast.application.dto.request.EnhancedSubscriptionRequestDTO;
import com.meli.notifier.forecast.domain.exception.NotFoundException;
import com.meli.notifier.forecast.domain.exception.ValidationException;
import com.meli.notifier.forecast.domain.mapper.SubscriptionMapper;
import com.meli.notifier.forecast.domain.model.database.Subscription;
import com.meli.notifier.forecast.domain.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CityRepository cityRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    @Transactional
    public Subscription createSubscription(UserEntity user, EnhancedSubscriptionRequestDTO request) {
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
        String cronExpression = buildEnhancedCronExpression(request);
        SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
                .user(user)
                .city(city)
                .cronExpression(cronExpression)
                .active(true)
                .build();
        SubscriptionEntity savedEntity = subscriptionRepository.save(subscriptionEntity);
        log.info("Subscription created successfully with id: {}", savedEntity.getId());
        return subscriptionMapper.toModel(savedEntity);
    }

    @Override
    public List<Subscription> getSubscriptions(UserEntity user) {
        return subscriptionRepository.findActiveByUserId(user.getId())
                .stream()
                .map(subscriptionMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Subscription getSubscription(UserEntity user, Long subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .map(subscriptionMapper::toModel);
    }

    @Override
    @Transactional
    public Subscription updateSubscription(UserEntity user, Long subscriptionId, EnhancedSubscriptionRequestDTO request) {
        SubscriptionEntity entity = subscriptionRepository.findById(subscriptionId)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NotFoundException("Subscription not found"));
        if (request.getCityId() != null && !request.getCityId().equals(entity.getCity().getIdCptec())) {
            CityEntity city = cityRepository.findById(request.getCityId())
                    .orElseThrow(() -> new NotFoundException("City not found with id: " + request.getCityId()));
            entity.setCity(city);
        }
        if (request.getScheduleType() != null || request.getCustomCronExpression() != null) {
            entity.setCronExpression(buildEnhancedCronExpression(request));
        }
        if (request.getTimezone() != null) {
            // Optionally handle timezone update if you store it
        }
        // Optionally handle other updatable fields (active, etc)
        SubscriptionEntity saved = subscriptionRepository.save(entity);
        return subscriptionMapper.toModel(saved);
    }

    @Override
    @Transactional
    public void deleteSubscription(UserEntity user, Long subscriptionId) {
        SubscriptionEntity entity = subscriptionRepository.findById(subscriptionId)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NotFoundException("Subscription not found"));
        subscriptionRepository.delete(entity);
    }

    private String buildEnhancedCronExpression(EnhancedSubscriptionRequestDTO request) {
        if (request.getScheduleType() == null) {
            return buildLegacyCronExpression(request);
        }
        switch(request.getScheduleType()) {
            case HOURLY:
                return buildHourlyCron(request);
            case DAILY:
                return buildDailyCron(request);
            case WEEKLY:
                return buildWeeklyCron(request);
            case MONTHLY:
                return buildMonthlyCron(request);
            case CUSTOM:
                return request.getCustomCronExpression();
            default:
                throw new ValidationException("Invalid schedule type");
        }
    }

    private String buildHourlyCron(EnhancedSubscriptionRequestDTO request) {
        int interval = request.getHourlyInterval() != null ? request.getHourlyInterval() : 1;
        int startHour = request.getHourlyStartingHour() != null ? request.getHourlyStartingHour() : 0;
        return String.format("0 0 %d/%d * * ? *", startHour, interval);
    }

    private String buildDailyCron(EnhancedSubscriptionRequestDTO request) {
        if (request.getDailyHours() != null && !request.getDailyHours().isEmpty()) {
            String hours = request.getDailyHours().stream().map(String::valueOf).collect(Collectors.joining(","));
            // "0 0 hours * * ? *" (at specific hours every day)
            return String.format("0 0 %s * * ? *", hours);
        }
        // Default: every day at 8am
        return "0 0 8 * * ? *";
    }

    private String buildWeeklyCron(EnhancedSubscriptionRequestDTO request) {
        if (request.getWeeklyDays() != null && !request.getWeeklyDays().isEmpty()) {
            String days = request.getWeeklyDays().stream().map(String::valueOf).collect(Collectors.joining(","));
            int hour = request.getWeeklyHour() != null ? request.getWeeklyHour() : 8;
            // "0 0 hour ? * days *" (at hour on specified days of week)
            return String.format("0 0 %d ? * %s *", hour, days);
        }
        // Default: every Monday at 8am
        return "0 0 8 ? * 1 *";
    }

    private String buildMonthlyCron(EnhancedSubscriptionRequestDTO request) {
        if (request.getMonthlyDays() != null && !request.getMonthlyDays().isEmpty()) {
            String days = request.getMonthlyDays().stream().map(String::valueOf).collect(Collectors.joining(","));
            int hour = request.getMonthlyHour() != null ? request.getMonthlyHour() : 8;
            // "0 0 hour days * ? *" (at hour on specified days of month)
            return String.format("0 0 %d %s * ? *", hour, days);
        }
        // Default: first day of month at 8am
        return "0 0 8 1 * ? *";
    }

    private String buildLegacyCronExpression(EnhancedSubscriptionRequestDTO request) {
        StringBuilder cron = new StringBuilder();
        cron.append(getOrDefault(request.getMinuteRepetition(), "0")).append(" ");
        cron.append("*").append(" "); // hour
        cron.append("*").append(" "); // day of month
        cron.append("*").append(" "); // month
        cron.append("?").append(" "); // day of week
        cron.append("*"); // year
        return cron.toString();
    }

    private String getOrDefault(String value, String defaultValue) {
        return (value == null || value.trim().isEmpty()) ? defaultValue : value.trim();
    }
}
