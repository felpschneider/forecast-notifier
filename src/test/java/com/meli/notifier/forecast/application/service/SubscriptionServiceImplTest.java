package com.meli.notifier.forecast.application.service;

import com.meli.notifier.forecast.adapter.out.persistence.repository.SubscriptionRepository;
import com.meli.notifier.forecast.application.port.in.CityService;
import com.meli.notifier.forecast.application.port.in.CptecService;
import com.meli.notifier.forecast.domain.entity.SubscriptionEntity;
import com.meli.notifier.forecast.domain.event.SubscriptionEvent;
import com.meli.notifier.forecast.domain.exception.NotFoundException;
import com.meli.notifier.forecast.domain.exception.ValidationException;
import com.meli.notifier.forecast.domain.mapper.SubscriptionMapper;
import com.meli.notifier.forecast.domain.model.database.City;
import com.meli.notifier.forecast.domain.model.database.Subscription;
import com.meli.notifier.forecast.domain.model.database.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private CityService cityService;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private CptecService cptecService;

    private SubscriptionServiceImpl subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionServiceImpl(
                subscriptionRepository,
                cityService,
                subscriptionMapper,
                eventPublisher,
                cptecService
        );
    }

    @Test
    void givenValidSubscriptionRequest_whenCreateSubscription_thenSubscriptionCreatedAndEventPublished() {
        // Arrange
        User user = buildUser();
        City city = getCity();

        Subscription requestSubscription = Subscription.builder()
                .city(city)
                .build();

        SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.setId(1L);

        Subscription savedSubscription = Subscription.builder()
                .id(1L)
                .user(user)
                .city(city)
                .active(true)
                .build();

        when(cityService.findById(city.getIdCptec())).thenReturn(Optional.of(city));
        when(subscriptionRepository.findByUserIdAndCityId(user.getId(), city.getIdCptec()))
                .thenReturn(Optional.empty());
        when(subscriptionMapper.toEntity(any(Subscription.class))).thenReturn(subscriptionEntity);
        when(subscriptionRepository.save(subscriptionEntity)).thenReturn(subscriptionEntity);
        when(subscriptionMapper.toModel(subscriptionEntity)).thenReturn(savedSubscription);

        // Act
        Subscription result = subscriptionService.createSubscription(user, requestSubscription);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertTrue(result.getActive());
        assertEquals(user, result.getUser());
        assertEquals(city, result.getCity());

        verify(subscriptionRepository).findByUserIdAndCityId(user.getId(), city.getIdCptec());
        verify(subscriptionMapper).toEntity(any(Subscription.class));
        verify(subscriptionRepository).save(subscriptionEntity);
        verify(subscriptionMapper, times(1)).toModel(subscriptionEntity);
        verify(eventPublisher).publishEvent(any(SubscriptionEvent.SubscriptionSaved.class));
        verify(cityService).saveIfNotExists(city);
    }

    @Test
    void givenMissingCityId_whenCreateSubscription_thenThrowValidationException() {
        // Arrange
        User user = buildUser();
        City cityWithoutId = buildCityWithoutId();

        Subscription requestSubscription = Subscription.builder()
                .city(cityWithoutId)
                .build();

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> subscriptionService.createSubscription(user, requestSubscription));

        assertEquals("City ID is required", exception.getMessage());
        verifyNoInteractions(subscriptionRepository, subscriptionMapper, eventPublisher);
    }

    @Test
    void givenMissingCityName_whenCreateSubscription_thenThrowValidationException() {
        // Arrange
        User user = buildUser();
        City cityWithoutName = City.builder().idCptec(123L).stateCode("SP").build();

        Subscription requestSubscription = Subscription.builder()
                .city(cityWithoutName)
                .build();

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> subscriptionService.createSubscription(user, requestSubscription));

        assertEquals("City ID is required", exception.getMessage());
        verifyNoInteractions(subscriptionRepository, subscriptionMapper, eventPublisher);
    }

    @Test
    void givenUserOptedOut_whenCreateSubscription_thenThrowValidationException() {
        // Arrange
        User user = User.builder().id(1L).email("user@example.com").optIn(false).build();
        City city = getCity();

        Subscription requestSubscription = Subscription.builder()
                .city(city)
                .build();

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> subscriptionService.createSubscription(user, requestSubscription));

        assertEquals("User opted out for notifications", exception.getMessage());
        verifyNoInteractions(subscriptionRepository, subscriptionMapper, eventPublisher);
    }

    @Test
    void givenExistingSubscription_whenCreateSubscription_thenThrowValidationException() {
        // Arrange
        User user = buildUser();
        City city = getCity();

        Subscription requestSubscription = Subscription.builder()
                .city(city)
                .build();

        SubscriptionEntity existingEntity = new SubscriptionEntity();
        Subscription existingSubscription = Subscription.builder()
                .id(1L)
                .user(user)
                .city(city)
                .active(true)
                .build();

        when(cityService.findById(city.getIdCptec())).thenReturn(Optional.of(city));
        when(subscriptionRepository.findByUserIdAndCityId(user.getId(), city.getIdCptec()))
                .thenReturn(Optional.of(existingEntity));
        when(subscriptionMapper.toModel(existingEntity)).thenReturn(existingSubscription);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> subscriptionService.createSubscription(user, requestSubscription));

        assertTrue(exception.getMessage().contains("This User Subscription already exists for city"));
        verify(cityService).findById(city.getIdCptec());
        verify(cityService).saveIfNotExists(city);
        verify(subscriptionRepository).findByUserIdAndCityId(user.getId(), city.getIdCptec());
        verify(subscriptionMapper).toModel(existingEntity);
        verify(subscriptionRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void givenUnknownCityWithValidCptecData_whenCreateSubscription_thenFetchFromCptecAndSave() {
        // Arrange
        User user = buildUser();
        City city = getCity();

        Subscription requestSubscription = Subscription.builder()
                .city(city)
                .build();

        SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.setId(1L);

        Subscription savedSubscription = Subscription.builder()
                .id(1L)
                .user(user)
                .city(city)
                .active(true)
                .build();

        when(cityService.findById(city.getIdCptec())).thenReturn(Optional.empty());
        when(cptecService.findCities(city.getName())).thenReturn(List.of(city));
        when(subscriptionRepository.findByUserIdAndCityId(user.getId(), city.getIdCptec()))
                .thenReturn(Optional.empty());
        when(subscriptionMapper.toEntity(any(Subscription.class))).thenReturn(subscriptionEntity);
        when(subscriptionRepository.save(subscriptionEntity)).thenReturn(subscriptionEntity);
        when(subscriptionMapper.toModel(subscriptionEntity)).thenReturn(savedSubscription);

        // Act
        Subscription result = subscriptionService.createSubscription(user, requestSubscription);

        // Assert
        assertNotNull(result);
        verify(cityService).findById(city.getIdCptec());
        verify(cptecService).findCities(city.getName());
        verify(cityService).saveIfNotExists(city);
        verify(subscriptionRepository).save(subscriptionEntity);
        verify(eventPublisher).publishEvent(any(SubscriptionEvent.SubscriptionSaved.class));
    }

    @Test
    void givenUnknownCityNotInCptec_whenCreateSubscription_thenThrowNotFoundException() {
        // Arrange
        User user = buildUser();
        City city = getCity();

        Subscription requestSubscription = Subscription.builder()
                .city(city)
                .build();

        when(cityService.findById(city.getIdCptec())).thenReturn(Optional.empty());
        when(cptecService.findCities(city.getName())).thenReturn(Collections.emptyList());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> subscriptionService.createSubscription(user, requestSubscription));

        assertTrue(exception.getMessage().contains("was not found in CPTEC"));
        verify(cityService).findById(city.getIdCptec());
        verify(cptecService).findCities(city.getName());
        verifyNoInteractions(subscriptionMapper, eventPublisher);
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void givenCityIdNotFoundInCptecResults_whenCreateSubscription_thenThrowNotFoundException() {
        // Arrange
        User user = buildUser();
        City requestCity = getCity();
        City differentCity = City.builder().idCptec(456L).name("São Paulo").stateCode("SP").build();

        Subscription requestSubscription = Subscription.builder()
                .city(requestCity)
                .build();

        when(cityService.findById(requestCity.getIdCptec())).thenReturn(Optional.empty());
        when(cptecService.findCities(requestCity.getName())).thenReturn(List.of(differentCity));

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> subscriptionService.createSubscription(user, requestSubscription));

        assertTrue(exception.getMessage().contains("City not found in the returned cities"));
        verify(cityService).findById(requestCity.getIdCptec());
        verify(cptecService).findCities(requestCity.getName());
        verify(subscriptionRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void givenActiveSubscriptions_whenFindAllByActiveIsTrue_thenReturnSubscriptions() {
        // Arrange
        List<SubscriptionEntity> entities = Arrays.asList(
                new SubscriptionEntity(),
                new SubscriptionEntity()
        );

        List<Subscription> expected = Arrays.asList(
                Subscription.builder().id(1L).build(),
                Subscription.builder().id(1L).build()
        );

        when(subscriptionRepository.findAllByActiveIsTrue()).thenReturn(entities);
        when(subscriptionMapper.toModel(entities.get(0))).thenReturn(expected.get(0));
        when(subscriptionMapper.toModel(entities.get(1))).thenReturn(expected.get(1));

        // Act
        List<Subscription> result = subscriptionService.findAllByActiveIsTrue();

        // Assert
        assertEquals(2, result.size());
        assertEquals(expected, result);
        verify(subscriptionRepository).findAllByActiveIsTrue();
    }

    @Test
    void givenUserWithSubscriptions_whenFindAllByUser_thenReturnUserSubscriptions() {
        // Arrange
        User user = User.builder().id(1L).email("user@example.com").build();

        List<SubscriptionEntity> entities = Arrays.asList(
                new SubscriptionEntity(),
                new SubscriptionEntity()
        );

        List<Subscription> expected = Arrays.asList(
                Subscription.builder().id(1L).build(),
                Subscription.builder().id(1L).build()
        );

        when(subscriptionRepository.findAllByUserId(user.getId())).thenReturn(entities);
        when(subscriptionMapper.toModel(entities.get(0))).thenReturn(expected.get(0));
        when(subscriptionMapper.toModel(entities.get(1))).thenReturn(expected.get(1));

        // Act
        List<Subscription> result = subscriptionService.findAllByUser(user);

        // Assert
        assertEquals(2, result.size());
        assertEquals(expected, result);
        verify(subscriptionRepository).findAllByUserId(user.getId());
    }

    @Test
    void givenSubscriptionId_whenFindById_thenReturnSubscription() {
        // Arrange
        Long subscriptionId = 1L;
        SubscriptionEntity entity = new SubscriptionEntity();
        Subscription expected = Subscription.builder().id(subscriptionId).build();

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(entity));
        when(subscriptionMapper.toModel(entity)).thenReturn(expected);

        // Act
        Optional<Subscription> result = subscriptionService.findById(subscriptionId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
        verify(subscriptionRepository).findById(subscriptionId);
    }

    @Test
    void givenNonExistentId_whenFindById_thenReturnEmpty() {
        // Arrange
        Long subscriptionId = 999L;
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        // Act
        Optional<Subscription> result = subscriptionService.findById(subscriptionId);

        // Assert
        assertTrue(result.isEmpty());
        verify(subscriptionRepository).findById(subscriptionId);
        verify(subscriptionMapper, never()).toModel(any(SubscriptionEntity.class));
    }

    @Test
    void givenActiveSubscriptionId_whenFindByIdAndActiveTrue_thenReturnSubscription() {
        // Arrange
        Long subscriptionId = 1L;
        SubscriptionEntity entity = new SubscriptionEntity();
        Subscription expected = Subscription.builder().id(subscriptionId).active(true).build();

        when(subscriptionRepository.findByIdAndActiveTrue(subscriptionId)).thenReturn(Optional.of(entity));
        when(subscriptionMapper.toModel(entity)).thenReturn(expected);

        // Act
        Optional<Subscription> result = subscriptionService.findByIdAndActiveTrue(subscriptionId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
        verify(subscriptionRepository).findByIdAndActiveTrue(subscriptionId);
    }

    @Test
    void givenSubscriptionIdAndUser_whenFindByIdAndUser_thenReturnSubscription() {
        // Arrange
        Long subscriptionId = 1L;
        User user = User.builder().id(1L).email("user@example.com").build();
        SubscriptionEntity entity = new SubscriptionEntity();
        Subscription expected = Subscription.builder().id(subscriptionId).user(user).build();

        when(subscriptionRepository.findByIdAndUserId(subscriptionId, user.getId())).thenReturn(Optional.of(entity));
        when(subscriptionMapper.toModel(entity)).thenReturn(expected);

        // Act
        Optional<Subscription> result = subscriptionService.findByIdAndUser(subscriptionId, user);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
        verify(subscriptionRepository).findByIdAndUserId(subscriptionId, user.getId());
    }

    @Test
    void givenValidSubscriptionIdAndUser_whenDeactivateSubscription_thenSubscriptionDeactivatedAndEventPublished() {
        // Arrange
        Long subscriptionId = 1L;
        User user = buildUser();

        SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.setId(subscriptionId);
        subscriptionEntity.setActive(true);

        when(subscriptionRepository.findByIdAndUserId(subscriptionId, user.getId()))
                .thenReturn(Optional.of(subscriptionEntity));
        when(subscriptionRepository.save(subscriptionEntity)).thenReturn(subscriptionEntity);

        // Act
        subscriptionService.deactivateSubscription(subscriptionId, user);

        // Assert
        assertFalse(subscriptionEntity.getActive());
        verify(subscriptionRepository).findByIdAndUserId(subscriptionId, user.getId());
        verify(subscriptionRepository).save(subscriptionEntity);
        verify(eventPublisher).publishEvent(any(SubscriptionEvent.SubscriptionDeleted.class));
    }

    @Test
    void givenNonExistentSubscription_whenDeactivateSubscription_thenThrowNotFoundException() {
        // Arrange
        Long subscriptionId = 999L;
        User user = buildUser();

        when(subscriptionRepository.findByIdAndUserId(subscriptionId, user.getId()))
                .thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> subscriptionService.deactivateSubscription(subscriptionId, user));

        assertEquals("Subscription not found with id: " + subscriptionId, exception.getMessage());
        verify(subscriptionRepository).findByIdAndUserId(subscriptionId, user.getId());
        verify(subscriptionRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void givenUserIdAndCityId_whenFindByUserIdAndCityId_thenReturnSubscription() {
        // Arrange
        Long userId = 1L;
        Long cityId = 123L;
        SubscriptionEntity entity = new SubscriptionEntity();
        Subscription expected = Subscription.builder().id(1L).build();

        when(subscriptionRepository.findByUserIdAndCityId(userId, cityId)).thenReturn(Optional.of(entity));
        when(subscriptionMapper.toModel(entity)).thenReturn(expected);

        // Act
        Optional<Subscription> result = subscriptionService.findByUserIdAndCityId(userId, cityId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
        verify(subscriptionRepository).findByUserIdAndCityId(userId, cityId);
    }

    @Test
    void givenPaginationParams_whenFindActiveSubscriptionsWithPagination_thenReturnPaginatedSubscriptions() {
        // Arrange
        int offset = 0;
        int limit = 10;

        SubscriptionEntity entity1 = new SubscriptionEntity();
        entity1.setId(1L);

        SubscriptionEntity entity2 = new SubscriptionEntity();
        entity2.setId(2L);

        List<SubscriptionEntity> entities = Arrays.asList(entity1, entity2);

        Subscription subscription1 = Subscription.builder().id(1L).build();
        Subscription subscription2 = Subscription.builder().id(2L).build();
        List<Subscription> expected = Arrays.asList(subscription1, subscription2);

        when(subscriptionRepository.findAllByActiveIsTrueWithPagination(offset, limit)).thenReturn(entities);
        when(subscriptionMapper.toModel(entity1)).thenReturn(subscription1);
        when(subscriptionMapper.toModel(entity2)).thenReturn(subscription2);

        // Act
        List<Subscription> result = subscriptionService.findActiveSubscriptionsWithPagination(offset, limit);

        // Assert
        assertEquals(2, result.size());
        assertEquals(expected, result);
        verify(subscriptionRepository).findAllByActiveIsTrueWithPagination(offset, limit);
        verify(subscriptionMapper).toModel(entity1);
        verify(subscriptionMapper).toModel(entity2);
    }

    @Test
    void givenNoActiveSubscriptions_whenFindAllByActiveIsTrue_thenReturnEmptyList() {
        // Arrange
        when(subscriptionRepository.findAllByActiveIsTrue()).thenReturn(Collections.emptyList());

        // Act
        List<Subscription> result = subscriptionService.findAllByActiveIsTrue();

        // Assert
        assertTrue(result.isEmpty());
        verify(subscriptionRepository).findAllByActiveIsTrue();
    }

    private static User buildUser() {
        return User.builder().id(1L).email("user@example.com").optIn(true).build();
    }

    private static City buildCityWithoutId() {
        return City.builder().name("São Paulo").stateCode("SP").build();
    }

    private static City getCity() {
        return City.builder().idCptec(123L).name("São Paulo").stateCode("SP").build();
    }
}
