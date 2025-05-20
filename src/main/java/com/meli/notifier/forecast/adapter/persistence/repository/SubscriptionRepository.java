package com.meli.notifier.forecast.adapter.persistence.repository;

import com.meli.notifier.forecast.adapter.persistence.entity.SubscriptionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    
    @Query("SELECT s FROM SubscriptionEntity s WHERE s.user.id = :userId AND s.city.idCptec = :cityId")
    Optional<SubscriptionEntity> findByUserIdAndCityId(@Param("userId") Long userId, @Param("cityId") Long cityId);
    
    @Query("SELECT s FROM SubscriptionEntity s WHERE s.user.id = :userId AND s.active = true")
    List<SubscriptionEntity> findActiveByUserId(@Param("userId") Long userId);
    
    @Query("SELECT s FROM SubscriptionEntity s WHERE s.active = true " +
           "AND (s.lastSentAt IS NULL OR s.lastSentAt < :currentTime)")
    List<SubscriptionEntity> findSubscriptionsToProcess(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT s FROM SubscriptionEntity s WHERE s.active = true AND s.user.optIn = true")
    List<SubscriptionEntity> findByActiveTrueAndUserOptInTrue();
}
