package com.meli.notifier.forecast.application.service;

import com.meli.notifier.forecast.domain.entity.NotificationChannelEntity;
import com.meli.notifier.forecast.domain.mapper.NotificationChannelMapper;
import com.meli.notifier.forecast.domain.model.database.NotificationChannel;
import com.meli.notifier.forecast.application.port.in.NotificationChannelService;
import com.meli.notifier.forecast.adapter.out.persistence.repository.NotificationChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationChannelServiceImpl implements NotificationChannelService {

    private final NotificationChannelRepository notificationChannelRepository;
    private final NotificationChannelMapper notificationChannelMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationChannel> getNotificationChannelByUserId(Long userId) {
        log.debug("Searching for notification channels user id {} opted in", userId);
        return notificationChannelRepository.findByUserId(userId)
                .map(notificationChannelMapper::toModel);
    }

    @Override
    @Transactional
    public NotificationChannel saveNotificationChannel(NotificationChannel notificationChannel) {
        log.debug("Saving notification channel : {}",
                  notificationChannel.getUser() != null ? notificationChannel.getUser().getId() : null);

        NotificationChannelEntity entity = notificationChannelMapper.toEntity(notificationChannel);
        NotificationChannelEntity savedEntity = notificationChannelRepository.save(entity);
        return notificationChannelMapper.toModel(savedEntity);
    }
}
