package com.meli.notifier.forecast.application.controller.websocket;

import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import com.meli.notifier.forecast.domain.service.NotificationService;
import com.meli.notifier.forecast.domain.service.impl.NotificationCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationCacheService notificationCacheService;
    private final NotificationService notificationService;

    /**
     * Handles ping messages from clients
     */
    @MessageMapping("/notifications/ping")
    @SendToUser("/queue/notifications/pong")
    public Map<String, Object> handlePing(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("type", "PONG");
        response.put("timestamp", System.currentTimeMillis());
        response.put("userId", principal.getName());
        
        // Check if there are any pending notifications for this user
        List<NotificationPayload> pendingNotifications = 
            notificationCacheService.getPendingNotifications(principal.getName());
            
        if (!pendingNotifications.isEmpty()) {
            log.info("Delivering {} pending notifications to user: {}", 
                pendingNotifications.size(), principal.getName());
                
            // Use the NotificationService with resilience patterns to deliver pending notifications
            pendingNotifications.forEach(notification -> 
                notificationService.sendNotificationToUser(notification));
        }
        
        log.debug("Ping-pong message for user: {}", principal.getName());
        return response;
    }
    
    /**
     * Acknowledges delivery of a notification
     */
    @MessageMapping("/notifications/ack")
    public void acknowledgeNotification(@Payload Map<String, Object> ackMessage, Principal principal) {
        try {
            Long subscriptionId = Long.valueOf(ackMessage.get("subscriptionId").toString());
            log.debug("Received ACK for notification subscriptionId={} from user: {}", 
                subscriptionId, principal.getName());
                
            // Find the notification and mark it as delivered
            notificationCacheService.getPendingNotifications(principal.getName()).stream()
                .filter(n -> n.getSubscriptionId().equals(subscriptionId))
                .findFirst()
                .ifPresent(notification -> 
                    notificationCacheService.markAsDelivered(principal.getName(), notification));
        } catch (Exception e) {
            log.error("Error processing notification ACK from user: {}", principal.getName(), e);
        }
    }
}
