package com.meli.notifier.forecast.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evento publicado quando um trigger de cron é acionado pelo Quartz.
 * Contém informações mínimas para processamento pelo consumer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriggerEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long subscriptionId;
    private LocalDateTime triggerTime;

    public static TriggerEvent create(Long subscriptionId) {
        return TriggerEvent.builder()
                .subscriptionId(subscriptionId)
                .triggerTime(LocalDateTime.now())
                .build();
    }
}
