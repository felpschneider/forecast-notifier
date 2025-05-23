package com.meli.notifier.forecast.adapter.out.persistence.repository;

import com.meli.notifier.forecast.domain.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {

    List<SubscriptionEntity> findAllByActiveIsTrue();

    @Query(value = "SELECT s FROM SubscriptionEntity s WHERE s.active = true ORDER BY s.id LIMIT :limit OFFSET :offset")
    List<SubscriptionEntity> findAllByActiveIsTrueWithPagination(@Param("offset") int offset, @Param("limit") int limit);

    @Query("SELECT s FROM SubscriptionEntity s WHERE s.user.id = :userId")
    List<SubscriptionEntity> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT s FROM SubscriptionEntity s WHERE s.id = :id AND s.user.id = :userId")
    Optional<SubscriptionEntity> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT s FROM SubscriptionEntity s WHERE s.user.id = :userId AND s.city.idCptec = :cityId")
    Optional<SubscriptionEntity> findByUserIdAndCityId(@Param("userId") Long userId, @Param("cityId") Long cityId);

    @Query("SELECT s FROM SubscriptionEntity s WHERE s.user.id = :userId AND s.active = true")
    List<SubscriptionEntity> findActiveByUserId(@Param("userId") Long userId);

    Optional<SubscriptionEntity> findByUserIdAndCityIdCptec(Long id, Long cityId);

    Optional<SubscriptionEntity> findByIdAndActiveTrue(Long id);
}
