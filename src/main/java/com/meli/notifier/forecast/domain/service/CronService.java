package com.meli.notifier.forecast.domain.service;

import java.time.LocalDateTime;

/**
 * Serviço para análise e validação de expressões cron
 */
public interface CronService {
    
    /**
     * Verifica se uma tarefa deve ser executada agora com base em sua expressão cron
     * e última execução
     * 
     * @param cronExpression A expressão cron no formato padrão Spring (segundo minuto hora diaMês mês diaSemana)
     * @param lastExecutionTime A data/hora da última execução, ou null se for a primeira execução
     * @param currentTime A data/hora atual para comparação
     * @return true se a tarefa deve ser executada agora, false caso contrário
     */
    boolean shouldExecuteNow(String cronExpression, LocalDateTime lastExecutionTime, LocalDateTime currentTime);
}
