package com.meli.notifier.forecast.domain.service.impl.notification;

import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import com.meli.notifier.forecast.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementação do padrão Strategy para diferentes canais de notificação.
 * Atualmente suporta apenas notificações web, mas está preparado para
 * expandir para outros canais como push, email e SMS no futuro.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationStrategyService {

    private final WebSocketNotificationService webSocketNotificationService;
    // Adicionar outros serviços de notificação quando implementados:
    // private final EmailNotificationService emailNotificationService;
    // private final PushNotificationService pushNotificationService;
    // private final SmsNotificationService smsNotificationService;

    // Map associando cada canal a seu respectivo serviço
    private final Map<NotificationChannelsEnum, NotificationService> notificationStrategies = new EnumMap<>(NotificationChannelsEnum.class);

    public void initStrategies() {
        notificationStrategies.put(NotificationChannelsEnum.WEB, webSocketNotificationService);
        // Adicionar outros canais quando implementados
        // notificationStrategies.put(NotificationChannelsEnum.EMAIL, emailNotificationService);
        // notificationStrategies.put(NotificationChannelsEnum.PUSH, pushNotificationService);
        // notificationStrategies.put(NotificationChannelsEnum.SMS, smsNotificationService);
    }

    /**
     * Envia uma notificação por todos os canais habilitados para o usuário.
     * Verifica quais canais estão ativos nas preferências do usuário antes de enviar.
     *
     * @param payload A notificação a ser enviada
     * @param enabledChannels Canais habilitados para o usuário
     */
    public void sendNotification(NotificationPayload payload, Set<NotificationChannelsEnum> enabledChannels) {
        if (enabledChannels == null || enabledChannels.isEmpty()) {
            log.warn("Não há canais habilitados para o usuário ID: {}", payload.getUserId());
            return;
        }

        // Inicializar o mapa de estratégias se ainda não foi feito
        if (notificationStrategies.isEmpty()) {
            initStrategies();
        }

        // Tenta enviar por cada canal habilitado
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

    /**
     * Versão simplificada que envia apenas por WebSocket.
     * Usado como fallback ou para retrocompatibilidade.
     */
    public void sendWebNotification(NotificationPayload payload) {
        if (notificationStrategies.isEmpty()) {
            initStrategies();
        }
        webSocketNotificationService.sendNotificationToUser(payload);
    }
}
