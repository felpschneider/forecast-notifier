//package com.meli.notifier.forecast.config;
//
//import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
//import io.github.resilience4j.retry.RetryConfig;
//import io.github.resilience4j.retry.RetryRegistry;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.Duration;
//
//@Configuration
//public class ResilienceConfig {
//
//    /**
//     * Configuração de retentativas para notificações WebSocket.
//     * - Máximo de 3 tentativas
//     * - Intervalo inicial de 1 segundo, com incremento de 50%
//     * - Retenta para qualquer exceção
//     */
//    @Bean
//    public RetryRegistry retryRegistry() {
//        RetryConfig webSocketNotificationConfig = RetryConfig.custom()
//                .maxAttempts(3)
//                .waitDuration(Duration.ofSeconds(1))
//                .retryExceptions(Exception.class)
//                .intervalFunction(attempt -> Math.round(attempt * 1.5 * 1000)) // Backoff exponencial
//                .build();
//
//        return RetryRegistry.of(
//
//        )
//
//        return RetryRegistry.of(
//                RetryConfig.custom().build(),
//                io.vavr.collection.Map.of("webSocketNotification", webSocketNotificationConfig)
//        );
//    }
//
//    /**
//     * Configuração de Circuit Breaker para notificações.
//     * Evita sobrecarregar o sistema quando há muitas falhas.
//     */
//    @Bean
//    public CircuitBreakerConfig circuitBreakerConfig() {
//        return CircuitBreakerConfig.custom()
//                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
//                .slidingWindowSize(10)
//                .failureRateThreshold(50)
//                .waitDurationInOpenState(Duration.ofMillis(5000))
//                .permittedNumberOfCallsInHalfOpenState(2)
//                .automaticTransitionFromOpenToHalfOpenEnabled(true)
//                .build();
//    }
//}
