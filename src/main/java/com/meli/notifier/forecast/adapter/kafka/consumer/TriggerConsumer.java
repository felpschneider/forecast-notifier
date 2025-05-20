//package com.meli.notifier.forecast.adapter.kafka.consumer;
//
//import com.meli.notifier.forecast.adapter.persistence.entity.SubscriptionEntity;
//import com.meli.notifier.forecast.adapter.persistence.repository.SubscriptionRepository;
//import com.meli.notifier.forecast.config.KafkaTopicConfig;
//import com.meli.notifier.forecast.domain.event.TriggerEvent;
//import com.meli.notifier.forecast.domain.exception.NotFoundException;
//import com.meli.notifier.forecast.domain.model.forecast.CombinedForecastDTO;
//import com.meli.notifier.forecast.domain.model.websocket.NotificationPayload;
//import com.meli.notifier.forecast.domain.service.CptecService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//
///**
// * Consumidor Kafka que processa eventos de trigger, obtém previsões do tempo
// * e publica notificações para os usuários.
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class TriggerConsumer {
//
//    private final SubscriptionRepository subscriptionRepository;
//    private final CptecService cptecService;
//    private final KafkaTemplate<String, NotificationPayload> kafkaTemplate;
//
//    /**
//     * Recebe eventos do tópico notification.triggers e processa cada um
//     * para gerar as notificações correspondentes. Configurado com concorrência
//     * para paralelizar o processamento.
//     *
//     * @param event O evento de trigger recebido
//     */
//    @KafkaListener(
//            groupId = "forecast-notifier-workers",
//            topics = KafkaTopicConfig.NOTIFICATION_TRIGGERS_TOPIC,
//            concurrency = "3"
//    )
//    public void processTriggerEvent(TriggerEvent event) {
//        Long subscriptionId = event.getSubscriptionId();
//        log.info("Processando evento de trigger para subscription ID: {}", subscriptionId);
//
//        try {
//            // 1. Buscar a subscription no banco de dados
//            SubscriptionEntity subscription = subscriptionRepository.findById(subscriptionId)
//                    .orElseThrow(() -> new NotFoundException("Subscription não encontrada: " + subscriptionId));
//
//            // Ignorar subscriptions inativas
//            if (!subscription.getActive()) {
//                log.info("Subscription {} está inativa. Ignorando evento.", subscriptionId);
//                return;
//            }
//
//            // 2. Buscar a previsão do tempo via CptecService
//            Long cityId = subscription.getCity().getIdCptec();
//            CombinedForecastDTO forecast = cptecService.getCombinedForecast(cityId);
//
//            // 3. Construir o payload da notificação
//            NotificationPayload payload = NotificationPayload.builder()
//                    .userId(subscription.getUser().getId())
//                    .subscriptionId(subscriptionId)
//                    .combinedForecast(forecast)
//                    .build();
//
//            // 4. Publicar a notificação no tópico de saída
//            kafkaTemplate.send(KafkaTopicConfig.NOTIFICATION_OUTBOUND_TOPIC,
//                    subscription.getUser().getId().toString(),
//                    payload);
//
//            // 5. Atualizar o timestamp de última execução
//            subscription.setLastSentAt(LocalDateTime.now());
//            subscriptionRepository.save(subscription);
//
//            log.info("Notificação processada e enviada para o usuário {} via subscription {}",
//                    subscription.getUser().getId(), subscriptionId);
//        } catch (Exception e) {
//            log.error("Erro ao processar evento de trigger para subscription {}", subscriptionId, e);
//            // Não relançar a exceção para evitar retry infinito
//            // Em um cenário produtivo, considerar uma dead letter queue
//        }
//    }
//}
