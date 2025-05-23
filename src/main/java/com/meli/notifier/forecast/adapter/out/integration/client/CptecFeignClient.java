package com.meli.notifier.forecast.adapter.out.integration.client;

import com.meli.notifier.forecast.adapter.out.integration.model.city.CityListResponseDTO;
import com.meli.notifier.forecast.adapter.out.integration.model.wave.WaveForecastResponseDTO;
import com.meli.notifier.forecast.adapter.out.integration.model.weather.ForecastResponseDTO;
import com.meli.notifier.forecast.config.XmlDecoderConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cptec-api", url = "${external.services.cptec.api.base-url}", configuration = XmlDecoderConfig.class)
public interface CptecFeignClient {

    @GetMapping("/listaCidades")
    CityListResponseDTO findCities(@RequestParam("city") String cityName);

    @GetMapping("/cidade/{cityId}/previsao.xml")
    ForecastResponseDTO getForecast(@PathVariable("cityId") Long cityId);

    @GetMapping("/cidade/{cityId}/dia/{day}/ondas.xml")
    WaveForecastResponseDTO getWaveForecast(@PathVariable("cityId") Long cityId, @PathVariable("day") Integer day);
}
