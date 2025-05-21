package com.meli.notifier.forecast.domain.mapper;

import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.database.NotificationChannel;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Componente responsável por mapear as preferências de notificação do usuário
 * para os canais habilitados correspondentes.
 */
@Component
public class NotificationPreferenceMapper {

    /**
     * Converte as preferências de notificação do usuário em um conjunto de canais habilitados.
     *
     * @param preferences Preferências de notificação do usuário
     * @return Conjunto de canais habilitados
     */
    public Set<NotificationChannelsEnum> mapToEnabledChannels(NotificationChannel preferences) {
        Set<NotificationChannelsEnum> enabledChannels = new HashSet<>();

        if (Boolean.TRUE.equals(preferences.getWebOptIn())) {
            enabledChannels.add(NotificationChannelsEnum.WEB);
        }

        if (Boolean.TRUE.equals(preferences.getEmail_opt_in())) {
            enabledChannels.add(NotificationChannelsEnum.EMAIL);
        }

        if (Boolean.TRUE.equals(preferences.getSms_opt_in())) {
            enabledChannels.add(NotificationChannelsEnum.SMS);
        }

        if (Boolean.TRUE.equals(preferences.getPush_opt_in())) {
            enabledChannels.add(NotificationChannelsEnum.PUSH);
        }

        return enabledChannels;
    }
}
