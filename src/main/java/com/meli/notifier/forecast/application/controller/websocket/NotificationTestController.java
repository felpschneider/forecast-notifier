package com.meli.notifier.forecast.application.controller.websocket;

import com.meli.notifier.forecast.domain.model.forecast.CombinedForecastDTO;
import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import com.meli.notifier.forecast.domain.service.impl.notification.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/notifications")
@Slf4j
public class NotificationTestController {

    private final WebSocketNotificationService notificationService;

    @PostMapping("/send/{userId}")
    public String sendTestNotification(@PathVariable Long userId, @RequestBody(required = false) String message) {
        log.info("Sending test notification to user: {}", userId);

        NotificationPayload payload = NotificationPayload.builder()
                .userId(userId)
                .subscriptionId(5L)
                .combinedForecast(new CombinedForecastDTO())
                .build();

        notificationService.sendNotificationToUser(payload);

        return "Notification sent to user: " + userId;
    }
}
