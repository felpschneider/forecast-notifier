package com.meli.notifier.forecast.adapter.integration.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Logger;
import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class CptecClientConfig {

    @Bean
    public Logger.Level cptecFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Decoder cptecFeignDecoder() {
        ObjectMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.registerModule(new JavaTimeModule());

        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter(xmlMapper);
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(messageConverter);

        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }
}

