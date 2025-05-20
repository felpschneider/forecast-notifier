package com.meli.notifier.forecast.adapter.messaging;

import com.meli.notifier.forecast.adapter.persistence.entity.SubscriptionEntity;
import com.meli.notifier.forecast.adapter.persistence.repository.SubscriptionRepository;
import com.meli.notifier.forecast.config.KafkaTopicConfig;
import com.meli.notifier.forecast.domain.event.TriggerEvent;
import com.meli.notifier.forecast.domain.model.forecast.CombinedForecastDTO;
import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
import com.meli.notifier.forecast.domain.service.CptecService;
import com.meli.notifier.forecast.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Consumidor de eventos de trigger que processa as notificações
 * disparadas pelos jobs do Quartz Scheduler.
 * 
 * Recupera os detalhes da subscrição, busca os dados climáticos
 * via serviço externo CPTEC, e publica a notificação completa
 * no tópico outbound para entrega via WebSocket.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TriggerConsumer {

    private final SubscriptionRepository subscriptionRepository;
    private final CptecService cptecService;
    private final EventPublisherPort eventPublisher;

    /**
     * Processa eventos de trigger do tópico notification.triggers.
     * Configurado com concurrency para processar múltiplas mensagens em paralelo
     * e com confirmação manual para garantir processamento confiável.
     * 
     * @param event O evento de trigger recebido
     * @param key A chave da mensagem (subscriptionId)
     * @param ack Objeto de confirmação manual
     */
    @KafkaListener(
            topics = KafkaTopicConfig.NOTIFICATION_TRIGGERS_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "3",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeTriggerEvent(
            @Payload TriggerEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment ack) {
        
        Long subscriptionId = event.getSubscriptionId();
        log.info("Recebido evento de trigger para subscrição ID: {}", subscriptionId);
        
        try {
            // Buscar detalhes da subscrição
            Optional<SubscriptionEntity> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
            
            if (subscriptionOpt.isEmpty() || !subscriptionOpt.get().getActive()) {
                log.warn("Subscrição ID {} não encontrada ou inativa. Ignorando evento.", subscriptionId);
                ack.acknowledge(); // Confirmar mesmo quando ignorado
                return;
            }
            
            SubscriptionEntity subscription = subscriptionOpt.get();
            
            // Obter dados de previsão do tempo do CPTEC
            CombinedForecastDTO forecast = cptecService.getCombinedForecast(subscription.getCity().getIdCptec());
            
            // Criar payload da notificação
            NotificationPayload notificationPayload = NotificationPayload.builder()
                    .userId(subscription.getUser().getId())
                    .subscriptionId(subscriptionId)
                    .combinedForecast(forecast)
                    .build();
            
            // Atualizar lastSentAt na subscrição
            subscription.setLastSentAt(LocalDateTime.now());
            subscriptionRepository.save(subscription);
            
            // Publicar evento de notificação para entrega via WebSocket
            eventPublisher.publishNotification(subscription.getUser().getId().toString(), notificationPayload);
            
            log.info("Notificação processada e publicada para usuário: {}, cidade: {}", 
                    subscription.getUser().getId(), forecast.getCityName());
            
            // Confirmar processamento bem-sucedido
            ack.acknowledge();
            
        } catch (Exception e) {
            log.error("Erro ao processar evento de trigger para subscrição ID: {}", subscriptionId, e);
            // Não confirmar para permitir retry pelo Kafka
            throw e;
        }
    }
}
