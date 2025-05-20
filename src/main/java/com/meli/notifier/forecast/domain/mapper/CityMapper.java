package com.meli.notifier.forecast.domain.mapper;

import com.meli.notifier.forecast.adapter.integration.model.city.CityResponseCptecDTO;
import com.meli.notifier.forecast.adapter.persistence.entity.CityEntity;
import com.meli.notifier.forecast.application.dto.response.CityResponseDTO;
import com.meli.notifier.forecast.domain.model.database.City;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {SubscriptionMapper.class})
public interface CityMapper {

    CityMapper INSTANCE = Mappers.getMapper(CityMapper.class);

    City toModel(CityEntity entity);

    CityEntity toEntity(City model);    CityResponseDTO toResponseDTO(City entity);

    CityResponseDTO toResponseDTO(CityEntity entity);

    List<CityResponseDTO> toResponseDTOs(List<City> entities);

    @Mapping(target = "idCptec", source = "id")
    City toModel(CityResponseCptecDTO dto);

    List<City> toModel(List<CityResponseCptecDTO> dto);
}
