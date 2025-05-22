package com.meli.notifier.forecast.domain.mapper;

import com.meli.notifier.forecast.adapter.out.integration.model.city.CityResponseCptecDTO;
import com.meli.notifier.forecast.domain.dto.request.CityRequestDTO;
import com.meli.notifier.forecast.domain.dto.response.CityResponseDTO;
import com.meli.notifier.forecast.domain.entity.CityEntity;
import com.meli.notifier.forecast.domain.model.database.City;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {SubscriptionMapper.class})
public interface CityMapper {
    City toModel(CityEntity entity);

    CityEntity toEntity(City model);

    CityResponseDTO toDTO(City model);

    CityResponseDTO toDTO(CityEntity entity);

    @Mapping(target = "idCptec", source = "id")
    City toModel(CityResponseCptecDTO dto);

    List<City> toModel(List<CityResponseCptecDTO> dto);

    City toModel(CityRequestDTO dto);
}
