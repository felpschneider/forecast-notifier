package com.meli.notifier.forecast.domain.service.impl.notification;

import com.meli.notifier.forecast.domain.enums.NotificationChannelsEnum;
import com.meli.notifier.forecast.domain.model.database.NotificationChannel;
import com.meli.notifier.forecast.domain.model.NotificationPayload;
import com.meli.notifier.forecast.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsNotificationStrategyService implements NotificationService {

    // Aqui você injetaria seu serviço de SMS
    // private final SmsService smsService;

    @Override
    public void sendNotificationToUser(NotificationPayload notification) {
        String userId = String.valueOf(notification.getUserId());

        log.info("Sending SMS notification for userId: {}", userId);
        sendMessage(Long.valueOf(userId), notification);
    }

    @Override
    public NotificationChannelsEnum getChannel() {
        return NotificationChannelsEnum.SMS;
    }

    @Override
    public boolean isEnabled(NotificationChannel preferences) {
        return Boolean.TRUE.equals(preferences.getSms_opt_in());
    }

    private void sendMessage(Long userId, NotificationPayload notification) {
        try {
            // Implementação do envio de SMS
            // smsService.sendSms(userId, createSmsFromNotification(notification));
            log.debug("SMS Notification successfully sent to userId: {}", userId);
        } catch (Exception e) {
            log.error("SMS Notification couldn't be sent to userId: {}", userId, e);
            throw e;
        }
    }

    // Método para converter NotificationPayload para formato de SMS
    // private SmsMessage createSmsFromNotification(NotificationPayload notification) {
    //    // Implementação de conversão
    // }
}
