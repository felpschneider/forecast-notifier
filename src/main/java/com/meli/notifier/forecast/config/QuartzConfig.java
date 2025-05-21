package com.meli.notifier.forecast.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("application.yml"));

        Properties props = new Properties();
        props.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
        propertiesFactoryBean.setProperties(props);
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactory(DataSource dataSource,
                                                 SpringAutowireJobFactory jobFactory,
                                                 @Qualifier("quartzProperties") Properties quartzProps) {

        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource); // datasource do PostgreSQL
        factory.setQuartzProperties(quartzProps);
        factory.setJobFactory(jobFactory); // permite injeção de dependências
        factory.setStartupDelay(10); // espera 10s após boot para iniciar
        factory.setApplicationContextSchedulerContextKey("springContext");
        factory.setWaitForJobsToCompleteOnShutdown(true); // aguarda jobs terminarem no shutdown
        factory.setOverwriteExistingJobs(false); // não sobrescreve jobs existentes
        return factory;
    }

    @Bean
    public SpringAutowireJobFactory jobFactory() {
        return new SpringAutowireJobFactory();
    }
}