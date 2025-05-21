package com.meli.notifier.forecast.domain.service.impl;

import com.meli.notifier.forecast.adapter.out.persistence.entity.NotificationChannelEntity;
import com.meli.notifier.forecast.domain.mapper.NotificationChannelMapper;
import com.meli.notifier.forecast.domain.model.database.NotificationChannel;
import com.meli.notifier.forecast.domain.service.NotificationChannelService;
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
        log.debug("Buscando preferências de canais de notificação para o usuário ID: {}", userId);
        return notificationChannelRepository.findByUserId(userId)
                .map(notificationChannelMapper::toModel);
    }

    @Override
    @Transactional
    public NotificationChannel saveNotificationChannel(NotificationChannel notificationChannel) {
        log.debug("Salvando preferências de canais de notificação para o usuário ID: {}",
                  notificationChannel.getUser() != null ? notificationChannel.getUser().getId() : null);

        NotificationChannelEntity entity = notificationChannelMapper.toEntity(notificationChannel);
        NotificationChannelEntity savedEntity = notificationChannelRepository.save(entity);
        return notificationChannelMapper.toModel(savedEntity);
    }
}
