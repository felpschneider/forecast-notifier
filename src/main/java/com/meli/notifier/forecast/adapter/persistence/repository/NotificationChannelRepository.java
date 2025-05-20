package com.meli.notifier.forecast.adapter.persistence.repository;

import com.meli.notifier.forecast.adapter.persistence.entity.NotificationChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationChannelRepository extends JpaRepository<NotificationChannelEntity, Long> {
}

