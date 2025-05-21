package com.meli.notifier.forecast.domain.service.impl;

import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * Service for caching notification messages in Redis.
 * This allows for reliable delivery even when user sessions reconnect.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationCacheService {

    private static final String PENDING_NOTIFICATIONS_KEY = "notifications:pending:";
    private static final String DELIVERED_NOTIFICATIONS_KEY = "notifications:delivered:";
    private static final Duration PENDING_EXPIRATION = Duration.ofDays(1);
    private static final Duration DELIVERED_EXPIRATION = Duration.ofHours(12);

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Cache a notification for a user that hasn't been delivered yet
     */
    public void cachePendingNotification(String userId, NotificationPayload notification) {
        String key = PENDING_NOTIFICATIONS_KEY + userId;
        try {
            redisTemplate.opsForList().rightPush(key, notification);
            redisTemplate.expire(key, PENDING_EXPIRATION);
            log.debug("Cached pending notification for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to cache pending notification for user: {}", userId, e);
        }
    }

    /**
     * Get all pending notifications for a user
     */
    public List<NotificationPayload> getPendingNotifications(String userId) {
        String key = PENDING_NOTIFICATIONS_KEY + userId;
        try {
            Long size = redisTemplate.opsForList().size(key);
            if (size != null && size > 0) {
                return redisTemplate.opsForList().range(key, 0, size - 1)
                        .stream()
                        .filter(obj -> obj instanceof NotificationPayload)
                        .map(obj -> (NotificationPayload) obj)
                        .toList();
            }
        } catch (Exception e) {
            log.error("Failed to get pending notifications for user: {}", userId, e);
        }
        return List.of();
    }

    /**
     * Mark a notification as delivered
     */
    public void markAsDelivered(String userId, NotificationPayload notification) {
        String pendingKey = PENDING_NOTIFICATIONS_KEY + userId;
        String deliveredKey = DELIVERED_NOTIFICATIONS_KEY + userId;
        
        try {
            // Remove from pending list
            redisTemplate.opsForList().remove(pendingKey, 1, notification);
            
            // Add to delivered list with expiration
            redisTemplate.opsForList().rightPush(deliveredKey, notification);
            redisTemplate.expire(deliveredKey, DELIVERED_EXPIRATION);
            
            log.debug("Marked notification as delivered for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to mark notification as delivered for user: {}", userId, e);
        }
    }

    /**
     * Check if a notification was already delivered (to avoid duplicates)
     */
    public boolean wasDelivered(String userId, NotificationPayload notification) {
        String deliveredKey = DELIVERED_NOTIFICATIONS_KEY + userId;
        try {
            Long size = redisTemplate.opsForList().size(deliveredKey);
            if (size != null && size > 0) {
                return redisTemplate.opsForList().range(deliveredKey, 0, size - 1)
                        .stream()
                        .filter(obj -> obj instanceof NotificationPayload)
                        .map(obj -> (NotificationPayload) obj)
                        .anyMatch(n -> n.getSubscriptionId().equals(notification.getSubscriptionId()));
            }
        } catch (Exception e) {
            log.error("Failed to check if notification was delivered for user: {}", userId, e);
        }
        return false;
    }
}
