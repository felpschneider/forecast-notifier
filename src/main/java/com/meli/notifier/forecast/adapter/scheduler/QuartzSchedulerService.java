package com.meli.notifier.forecast.adapter.scheduler;

import com.meli.notifier.forecast.domain.model.database.Subscription;
import com.meli.notifier.forecast.domain.service.SubscriptionService;
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

    private final Scheduler scheduler;
    private final SubscriptionService subscriptionService;

    private static final String SUBSCRIPTION_ID_KEY = "subscriptionId";
    private static final String JOB_GROUP = "subscription-jobs";
    private static final String TRIGGER_GROUP = "subscription-triggers";

    @EventListener(ApplicationReadyEvent.class)
    public void scheduleAllActiveSubscriptions() {
        try {
            log.info("Iniciando agendamento de todas as subscrições ativas");
            List<Subscription> activeSubscriptions = subscriptionService
                    .findAllByActiveIsTrue();

            log.info("Encontradas {} subscrições ativas para agendar", activeSubscriptions.size());

            for (Subscription subscription : activeSubscriptions) {
                scheduleOrUpdateJob(subscription);
            }

            log.info("Agendamento inicial de subscrições concluído");
        } catch (Exception e) {
            log.error("Erro ao agendar subscrições ativas durante o startup", e);
        }
    }

    public void scheduleOrUpdateJob(Subscription subscription) {
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
            }
            // Agendar um novo job se não existir
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("Novo job agendado para a subscrição ID: {}", subscription.getId());
        } catch (SchedulerException e) {
            log.error("Erro ao agendar job para a subscrição ID: {}", subscription.getId(), e);
        }
    }

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
    private JobDetail buildJobDetail(Subscription subscription) {
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
    private CronTrigger buildCronTrigger(Subscription subscription) {
        return TriggerBuilder.newTrigger()
                .withIdentity("sub-" + subscription.getId(), TRIGGER_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(subscription.getCronExpression())
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();
    }
}
