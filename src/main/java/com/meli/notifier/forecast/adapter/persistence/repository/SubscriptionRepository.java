package com.meli.notifier.forecast.adapter.persistence.repository;

import com.meli.notifier.forecast.adapter.persistence.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {

    List<SubscriptionEntity> findAllByActiveIsTrue();

    @Query("SELECT s FROM SubscriptionEntity s WHERE s.user.id = :userId AND s.active = true")
    List<SubscriptionEntity> findActiveByUserId(@Param("userId") Long userId);

    @Query("SELECT s FROM SubscriptionEntity s WHERE s.user.id = :userId AND s.city.idCptec = :cityId")
    Optional<SubscriptionEntity> findByUserIdAndCityId(@Param("userId") Long userId, @Param("cityId") Long cityId);

    Optional<SubscriptionEntity> findByUserIdAndCityIdCptec(Long id, Long cityId);
}
