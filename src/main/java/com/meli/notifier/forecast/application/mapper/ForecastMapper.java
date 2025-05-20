package com.meli.notifier.forecast.application.mapper;

import com.meli.notifier.forecast.adapter.integration.model.wave.WaveForecastResponseDTO;
import com.meli.notifier.forecast.adapter.integration.model.weather.ForecastResponseDTO;
import com.meli.notifier.forecast.domain.model.forecast.CombinedForecastDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ForecastMapper {

    @Mapping(target = "cityName", source = "forecastResponseDTO.name")
    @Mapping(target = "stateCode", source = "forecastResponseDTO.stateCode")
    @Mapping(target = "updateDate", expression = "java(java.time.LocalDate.parse(forecastResponseDTO.getUpdateDate()))")
    @Mapping(target = "weatherForecast", source = "forecastResponseDTO.forecasts")
    @Mapping(target = "waveForecast", source = "waveForecast")
    CombinedForecastDTO toDTO(ForecastResponseDTO forecastResponseDTO, WaveForecastResponseDTO waveForecast);

}
