package com.meli.notifier.forecast.domain.model.forecast.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastDay {
    @JacksonXmlProperty(localName = "dia")
    private String date;

    @JacksonXmlProperty(localName = "tempo")
    private String weatherCode;

    @JacksonXmlProperty(localName = "maxima")
    private Integer maxTemperature;

    @JacksonXmlProperty(localName = "minima")
    private Integer minTemperature;

    @JacksonXmlProperty(localName = "iuv")
    private Double uvIndex;
}
