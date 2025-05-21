package com.meli.notifier.forecast.adapter.out.messaging.integration.model.city;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CityResponseCptecDTO {

    @JacksonXmlProperty(localName = "nome")
    private String name;

    @JacksonXmlProperty(localName = "uf")
    private String stateCode;

    @JacksonXmlProperty(localName = "id")
    private Long id;
}
