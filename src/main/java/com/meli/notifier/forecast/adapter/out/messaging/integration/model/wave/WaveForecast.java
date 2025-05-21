package com.meli.notifier.forecast.adapter.out.messaging.integration.model.wave;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaveForecast {
    @JacksonXmlProperty(localName = "dia")
    private String datetime;

    @JacksonXmlProperty(localName = "agitacao")
    private String intensity;

    @JacksonXmlProperty(localName = "altura")
    private Double height;

    @JacksonXmlProperty(localName = "direcao")
    private String direction;

    @JacksonXmlProperty(localName = "vento")
    private Double windSpeed;

    @JacksonXmlProperty(localName = "vento_dir")
    private String windDirection;
}
