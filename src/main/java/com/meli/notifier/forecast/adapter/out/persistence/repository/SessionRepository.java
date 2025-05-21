package com.meli.notifier.forecast.adapter.out.persistence.repository;

import com.meli.notifier.forecast.adapter.out.persistence.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, String> {

    Optional<SessionEntity> findByIdAndExpiresAtAfter(String token, LocalDateTime now);
}
