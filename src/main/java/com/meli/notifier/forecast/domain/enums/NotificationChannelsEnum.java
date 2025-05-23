package com.meli.notifier.forecast.domain.enums;

import com.meli.notifier.forecast.domain.exception.NotificationChannelException;
import lombok.Getter;

@Getter
public enum NotificationChannelsEnum {
    WEB("web"),
    EMAIL("email"),
    PUSH("push"),
    SMS("sms");

    private final String name;

    NotificationChannelsEnum(String name) {
        this.name = name;
    }

    public static NotificationChannelsEnum from(String channel) {
        for (NotificationChannelsEnum notificationChannel : NotificationChannelsEnum.values()) {
            if (notificationChannel.getName().equalsIgnoreCase(channel)) {
                return notificationChannel;
            }
        }
        throw new NotificationChannelException(String.format("Invalid channel: %s", channel));
    }
}
