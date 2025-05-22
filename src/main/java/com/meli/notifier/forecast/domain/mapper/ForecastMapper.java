package com.meli.notifier.forecast.domain.mapper;

import com.meli.notifier.forecast.domain.model.forecast.CombinedForecastDTO;
import com.meli.notifier.forecast.domain.model.forecast.WaveForecastWrapper;
import com.meli.notifier.forecast.domain.model.forecast.WeatherForecastWrapper;
import com.meli.notifier.forecast.domain.model.forecast.wave.WaveForecastResponseDTO;
import com.meli.notifier.forecast.domain.model.forecast.weather.ForecastResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ForecastMapper {

    @Mapping(target = "cityName", source = "forecastResponse.name")
    @Mapping(target = "stateCode", source = "forecastResponse.stateCode")
    @Mapping(target = "weatherForecast", source = "forecastResponse")
    @Mapping(target = "waveForecast", source = "waveForecast")
    CombinedForecastDTO toDTO(ForecastResponseDTO forecastResponse, WaveForecastResponseDTO waveForecast);

    @Mapping(target = "forecastDays", source = "weatherResponse.forecasts")
    WeatherForecastWrapper toWrapper(ForecastResponseDTO weatherResponse);

    WaveForecastWrapper toWrapper(WaveForecastResponseDTO waveForecast);
}
