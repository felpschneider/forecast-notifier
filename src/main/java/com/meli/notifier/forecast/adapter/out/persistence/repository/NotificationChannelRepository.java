package com.meli.notifier.forecast.adapter.out.persistence.repository;

import com.meli.notifier.forecast.domain.entity.NotificationChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationChannelRepository extends JpaRepository<NotificationChannelEntity, Long> {

    Optional<NotificationChannelEntity> findByUserId(Long userId);
}
