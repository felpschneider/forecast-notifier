package com.meli.notifier.forecast.application.service;

import com.meli.notifier.forecast.adapter.out.persistence.repository.SubscriptionRepository;
import com.meli.notifier.forecast.application.port.in.CityService;
import com.meli.notifier.forecast.application.port.in.CptecService;
import com.meli.notifier.forecast.application.port.in.SubscriptionService;
import com.meli.notifier.forecast.domain.entity.SubscriptionEntity;
import com.meli.notifier.forecast.domain.event.SubscriptionEvent;
import com.meli.notifier.forecast.domain.exception.NotFoundException;
import com.meli.notifier.forecast.domain.exception.ValidationException;
import com.meli.notifier.forecast.domain.mapper.SubscriptionMapper;
import com.meli.notifier.forecast.domain.model.database.City;
import com.meli.notifier.forecast.domain.model.database.Subscription;
import com.meli.notifier.forecast.domain.model.database.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.apache.logging.log4j.util.Strings.isEmpty;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CityService cityService;
    private final SubscriptionMapper subscriptionMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final CptecService cptecService;

    @Transactional
    @Override
    public Subscription createSubscription(User user, Subscription request) {
        log.info("Creating subscription for user id: {}, city id: {} and city name: {}", user.getId(), request.getCity().getIdCptec(), request.getCity().getName());
        if (request.getCity().getIdCptec() == null || isEmpty(request.getCity().getName())) {
            throw new ValidationException("City ID is required");
        }

        Optional<City> cityOpt = cityService.findById(request.getCity().getIdCptec());
        City city = cityOpt.orElseGet(() -> fetchCity(request));

        throwIfSubscriptionExists(user, city);

        Subscription subscription = request.toBuilder()
                .user(user)
                .city(city)
                .active(true)
                .build();

        SubscriptionEntity savedEntity = subscriptionRepository.save(subscriptionMapper.toEntity(subscription));
        Subscription sub = subscriptionMapper.toModel(savedEntity);
        eventPublisher.publishEvent(new SubscriptionEvent.SubscriptionSaved(sub));

        log.info("Subscription created successfully with id: {}", savedEntity.getId());
        return sub;
    }

    private City fetchCity(Subscription request) {
        City cityFromCptec = fetchCityFromCptec(request.getCity().getName(), request.getCity().getIdCptec());
        cityService.saveCity(cityFromCptec);
        return cityFromCptec;
    }

    private City fetchCityFromCptec(String cityName, Long cityId) {
        var cities = cptecService.findCities(cityName);
        if (cities.isEmpty()) {
            log.error("City with name: {} was not found in CPTEC", cityName);
            throw new NotFoundException(String.format("City with name: %s was not found in CPTEC", cityName));
        }

        return cities.stream().filter(city -> city.getIdCptec().equals(cityId)).findFirst()
                .orElseThrow(() -> {
                    log.error("City id: {} was not found in the returned cities", cityId);
                    return new NotFoundException("City not found in the returned cities of CPTEC. Either city name or city ID is incorrect");
                });
    }

    private void throwIfSubscriptionExists(User user, City city) {
        findByUserIdAndCityId(user.getId(), city.getIdCptec()).ifPresent(
                existingSubscription -> {
                    log.error("Subscription already exists for user id: {} and city id: {}", user.getId(), city.getIdCptec());
                    throw new ValidationException(String.format("This User Subscription already exists for city: %s", city.getName()));
                }
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<Subscription> findAllByActiveIsTrue() {
        log.info("Fetching all active subscriptions");
        return subscriptionRepository.findAllByActiveIsTrue().stream()
                .map(subscriptionMapper::toModel)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Subscription> findAllByUser(User user) {
        log.info("Fetching all subscriptions for user id: {}", user.getId());
        return subscriptionRepository.findAllByUserId(user.getId()).stream()
                .map(subscriptionMapper::toModel)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Subscription> findById(Long id) {
        log.debug("Finding subscription by id: {}", id);
        return subscriptionRepository.findById(id)
                .map(subscriptionMapper::toModel);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Subscription> findByIdAndActiveTrue(Long id) {
        log.debug("Finding active subscription by id: {}", id);
        return subscriptionRepository.findByIdAndActiveTrue((id))
                .map(subscriptionMapper::toModel);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Subscription> findByIdAndUser(Long id, User user) {
        log.info("Finding subscription by id: {} for user id: {}", id, user.getId());
        return subscriptionRepository.findByIdAndUserId(id, user.getId())
                .map(subscriptionMapper::toModel);
    }

    @Transactional
    @Override
    public void deactivateSubscription(Long id, User user) {
        log.info("Deactivating subscription id: {} for user id: {}", id, user.getId());

        SubscriptionEntity subscription = subscriptionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Subscription not found with id: " + id));

        subscription.setActive(false);
        subscriptionRepository.save(subscription);

        eventPublisher.publishEvent(new SubscriptionEvent.SubscriptionDeleted(id));

        log.info("Subscription successfully deactivated with id: {}", id);
    }

    @Override
    public Optional<Subscription> findByUserIdAndCityId(Long id, Long idCptec) {
        log.info("Finding subscription by user id: {} and city id: {}", id, idCptec);
        return subscriptionRepository.findByUserIdAndCityId(id, idCptec)
                .map(subscriptionMapper::toModel);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Subscription> findActiveSubscriptionsWithPagination(int offset, int limit) {
        log.info("Fetching active subscriptions with pagination - offset: {}, limit: {}", offset, limit);
        return subscriptionRepository.findAllByActiveIsTrueWithPagination(offset, limit).stream()
                .map(subscriptionMapper::toModel)
                .toList();
    }
}
