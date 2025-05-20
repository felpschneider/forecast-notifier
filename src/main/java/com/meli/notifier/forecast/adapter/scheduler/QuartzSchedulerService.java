package com.meli.notifier.forecast.adapter.scheduler;

import com.meli.notifier.forecast.adapter.persistence.entity.SubscriptionEntity;
import com.meli.notifier.forecast.adapter.persistence.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuartzSchedulerService {

//    private final SchedulerFactoryBean schedulerFactoryBean;
    private final Scheduler scheduler;
    private final SubscriptionRepository subscriptionRepository;

    private static final String SUBSCRIPTION_ID_KEY = "subscriptionId";
    private static final String JOB_GROUP = "subscription-jobs";
    private static final String TRIGGER_GROUP = "subscription-triggers";

    /**
     * Método executado após o startup da aplicação para carregar e agendar
     * todas as subscrições ativas no banco de dados.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void scheduleAllActiveSubscriptions() {
        try {
            log.info("Iniciando agendamento de todas as subscrições ativas");
            List<SubscriptionEntity> activeSubscriptions = subscriptionRepository
                    .findAllByActiveIsTrue();

            log.info("Encontradas {} subscrições ativas para agendar", activeSubscriptions.size());

            for (SubscriptionEntity subscription : activeSubscriptions) {
                scheduleOrUpdateJob(subscription);
            }

            log.info("Agendamento inicial de subscrições concluído");
        } catch (Exception e) {
            log.error("Erro ao agendar subscrições ativas durante o startup", e);
        }
    }

    /**
     * Agenda um novo job ou atualiza um existente para a subscrição fornecida.
     *
     * @param subscription A entidade de subscrição para agendar
     */
    public void scheduleOrUpdateJob(SubscriptionEntity subscription) {
        try {
            if (!subscription.getActive()) {
                deleteJob(subscription.getId());
                log.info("Subscrição {} está inativa, job removido", subscription.getId());
                return;
            }

            // Criar JobDetail com ID da subscrição
            JobDetail jobDetail = buildJobDetail(subscription);

            // Criar CronTrigger com a expressão cron da subscrição
            CronTrigger trigger = buildCronTrigger(subscription);

            // Verificar se o job já existe
            if (scheduler.checkExists(jobDetail.getKey())) {
                // Atualizar o trigger se o job já existir
                scheduler.rescheduleJob(trigger.getKey(), trigger);
                log.info("Job existente atualizado para a subscrição ID: {}",
                        subscription.getId());
            } else {
                // Agendar um novo job se não existir
                scheduler.scheduleJob(jobDetail, trigger);
                log.info("Novo job agendado para a subscrição ID: {}",
                        subscription.getId());
            }
        } catch (SchedulerException e) {
            log.error("Erro ao agendar job para a subscrição ID: {}",
                    subscription.getId(), e);
        }
    }

    /**
     * Remove um job agendado para a subscrição com o ID fornecido.
     *
     * @param subscriptionId ID da subscrição a ser removida
     */
    public void deleteJob(Long subscriptionId) {
        try {

            JobKey jobKey = new JobKey("sub-" + subscriptionId, JOB_GROUP);
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                log.info("Job removido para a subscrição ID: {}", subscriptionId);
            }
        } catch (SchedulerException e) {
            log.error("Erro ao remover job para a subscrição ID: {}",
                    subscriptionId, e);
        }
    }

    /**
     * Constrói um JobDetail para a subscrição fornecida.
     *
     * @param subscription A entidade de subscrição
     * @return O JobDetail configurado
     */
    private JobDetail buildJobDetail(SubscriptionEntity subscription) {
        return JobBuilder.newJob(CronTriggerJob.class)
                .withIdentity("sub-" + subscription.getId(), JOB_GROUP)
                .usingJobData(SUBSCRIPTION_ID_KEY, subscription.getId())
                .storeDurably()
                .build();
    }

    /**
     * Constrói um CronTrigger para a subscrição fornecida.
     *
     * @param subscription A entidade de subscrição
     * @return O CronTrigger configurado
     */
    private CronTrigger buildCronTrigger(SubscriptionEntity subscription) {
        return TriggerBuilder.newTrigger()
                .withIdentity("sub-" + subscription.getId(), TRIGGER_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(subscription.getCronExpression())
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();
    }
}
