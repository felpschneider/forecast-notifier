package com.meli.notifier.forecast.domain.service.impl;

import com.meli.notifier.forecast.domain.service.CronService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class CronServiceImpl implements CronService {

    @Override
    public boolean shouldExecuteNow(String cronExpression, LocalDateTime lastExecutionTime, LocalDateTime currentTime) {
        try {
            if (!CronExpression.isValidExpression(cronExpression)) {
                log.error("Expressão cron inválida: {}", cronExpression);
                return false;
            }
            
            CronExpression cron = CronExpression.parse(cronExpression);
            
            if (lastExecutionTime == null) {
                // Primeira execução
                return true;
            }
            
            // Encontrar a próxima data de execução após a última execução
            LocalDateTime nextExecution = cron.next(lastExecutionTime);
            
            // Se a próxima execução for antes ou igual ao momento atual, deve executar
            return nextExecution != null && (nextExecution.isBefore(currentTime) || nextExecution.isEqual(currentTime));
            
        } catch (Exception e) {
            log.error("Erro ao analisar expressão cron: {}", cronExpression, e);
            return false;
        }
    }
}
