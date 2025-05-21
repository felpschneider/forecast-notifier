package com.meli.notifier.forecast.adapter.out.messaging.integration.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "cidade")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastResponseDTO {

    @JacksonXmlProperty(localName = "nome")
    private String name;

    @JacksonXmlProperty(localName = "uf")
    private String stateCode;

    @JacksonXmlProperty(localName = "atualizacao")
    private String updateDate;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "previsao")
    private List<ForecastDay> forecasts;
}
