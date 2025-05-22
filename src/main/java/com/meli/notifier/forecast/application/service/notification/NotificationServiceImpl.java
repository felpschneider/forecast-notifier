package com.meli.notifier.forecast.application.service.notification;

import com.meli.notifier.forecast.application.port.in.notification.NotificationChannelService;
import com.meli.notifier.forecast.application.port.in.notification.NotificationService;
import com.meli.notifier.forecast.application.port.in.notification.NotificationStrategy;
import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.NotificationPayload;
import com.meli.notifier.forecast.domain.model.database.NotificationChannel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final Collection<NotificationStrategy> notificationStrategies;
    private final NotificationChannelService notificationChannelService;

    private final Map<NotificationChannelsEnum, NotificationStrategy> strategies = new EnumMap<>(NotificationChannelsEnum.class);

    @PostConstruct
    public void initStrategies() {
        notificationStrategies.forEach(provider -> strategies.put(provider.getChannel(), provider));

        log.info("Initialized {} notification channel providers", notificationStrategies.size());
    }

    @Override
    public void sendNotification(NotificationPayload payload) {
        if (payload.getUserId() == null) {
            log.error("Cannot send notification - user ID is null");
            return;
        }

        Optional<NotificationChannel> userChannelPrefs = notificationChannelService.getNotificationChannelByUserId(payload.getUserId());

        if (userChannelPrefs.isEmpty()) {
            log.warn("User {} has no notification preferences set. Using default (web only)", payload.getUserId());
            sendNotificationToEnabledChannels(payload, Set.of(NotificationChannelsEnum.WEB));
            return;
        }

        NotificationChannel preferences = userChannelPrefs.get();

        Set<NotificationChannelsEnum> enabledChannels = strategies.values().stream()
                .filter(provider -> provider.isEnabled(preferences))
                .map(NotificationStrategy::getChannel)
                .collect(Collectors.toSet());

        sendNotificationToEnabledChannels(payload, enabledChannels);
    }

    @Override
    public void sendNotificationToEnabledChannels(NotificationPayload payload, Set<NotificationChannelsEnum> enabledChannels) {
        if (enabledChannels == null || enabledChannels.isEmpty()) {
            log.warn("No channel enabled for user ID: {}", payload.getUserId());
            return;
        }

        if (notificationStrategies.isEmpty()) {
            initStrategies();
        }

        for (NotificationChannelsEnum channel : enabledChannels) {
            NotificationStrategy provider = strategies.get(channel);
            if (provider != null) {
                try {
                    provider.sendNotificationToUser(payload);
                    log.info("Notification sent through channel: {} for user ID: {}",
                            channel.getName(), payload.getUserId());
                    return;
                } catch (Exception e) {
                    log.error("Error sending notification through channel: {} for user ID: {}",
                            channel.getName(), payload.getUserId(), e);
                }
            }
        }
    }
}
