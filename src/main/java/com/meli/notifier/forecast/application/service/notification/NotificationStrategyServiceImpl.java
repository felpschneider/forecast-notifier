package com.meli.notifier.forecast.application.service.notification;

import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.database.NotificationChannel;
import com.meli.notifier.forecast.domain.model.NotificationPayload;
import com.meli.notifier.forecast.application.port.in.NotificationChannelService;
import com.meli.notifier.forecast.application.port.in.NotificationService;
import com.meli.notifier.forecast.application.port.in.NotificationStrategyService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationStrategyServiceImpl implements NotificationStrategyService {

    private final Collection<NotificationService> notificationServices;
    private final NotificationChannelService notificationChannelService;

    private final Map<NotificationChannelsEnum, NotificationService> notificationStrategies = new EnumMap<>(NotificationChannelsEnum.class);

    @PostConstruct
    public void initStrategies() {
        notificationServices.forEach(service -> notificationStrategies.put(service.getChannel(), service));

        log.info("Initialized {} notification strategies", notificationStrategies.size());
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
            sendWebNotification(payload);
            return;
        }

        NotificationChannel preferences = userChannelPrefs.get();

        Set<NotificationChannelsEnum> enabledChannels = notificationStrategies.values().stream()
                .filter(service -> service.isEnabled(preferences))
                .map(NotificationService::getChannel)
                .collect(Collectors.toSet());

        sendNotificationToEnabledChannels(payload, enabledChannels);
    }

    @Override
    public void sendNotificationToEnabledChannels(NotificationPayload payload, Set<NotificationChannelsEnum> enabledChannels) {
        if (enabledChannels == null || enabledChannels.isEmpty()) {
            log.warn("Não há canais habilitados para o usuário ID: {}", payload.getUserId());
            return;
        }

        if (notificationStrategies.isEmpty()) {
            initStrategies();
        }

        for (NotificationChannelsEnum channel : enabledChannels) {
            NotificationService service = notificationStrategies.get(channel);
            if (service != null) {
                try {
                    service.sendNotificationToUser(payload);
                    log.info("Notificação enviada via canal {} para usuário ID: {}",
                            channel.getName(), payload.getUserId());
                } catch (Exception e) {
                    log.error("Erro ao enviar notificação via canal {} para usuário ID: {}",
                            channel.getName(), payload.getUserId(), e);
                }
            } else {
                log.warn("Canal {} não implementado ou não disponível", channel.getName());
            }
        }
    }

    @Override
    public void sendWebNotification(NotificationPayload payload) {
        if (notificationStrategies.isEmpty()) {
            initStrategies();
        }

        NotificationService webService = notificationStrategies.get(NotificationChannelsEnum.WEB);
        if (webService != null) {
            webService.sendNotificationToUser(payload);
            log.info("Fallback notification sent through WebSocket for user ID: {}", payload.getUserId());
        } else {
            log.error("WebSocket service unavailable for fallback notification to user ID: {}", payload.getUserId());
        }
    }
}
