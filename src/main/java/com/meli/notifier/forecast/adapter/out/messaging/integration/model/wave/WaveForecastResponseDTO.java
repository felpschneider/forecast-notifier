package com.meli.notifier.forecast.adapter.out.messaging.integration.model.wave;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "cidade")
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaveForecastResponseDTO {

    @JacksonXmlProperty(localName = "nome")
    private String name;

    @JacksonXmlProperty(localName = "uf")
    private String stateCode;

    @JacksonXmlProperty(localName = "atualizacao")
    private String updateDate;

    @JacksonXmlProperty(localName = "manha")
    private WaveForecast morning;

    @JacksonXmlProperty(localName = "tarde")
    private WaveForecast afternoon;

    @JacksonXmlProperty(localName = "noite")
    private WaveForecast evening;
}
