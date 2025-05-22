package com.meli.notifier.forecast.domain.mapper;

import com.meli.notifier.forecast.domain.entity.SubscriptionEntity;
import com.meli.notifier.forecast.domain.dto.request.SubscriptionRequestDTO;
import com.meli.notifier.forecast.domain.dto.response.SubscriptionCreationResponseDTO;
import com.meli.notifier.forecast.domain.model.database.Subscription;
import com.meli.notifier.forecast.domain.model.database.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class, CityMapper.class})
public interface SubscriptionMapper {
    Subscription toModel(SubscriptionEntity entity);

    SubscriptionEntity toEntity(Subscription model);

    @Mapping(target = "user", source = "user")
    SubscriptionEntity toEntity(User user, SubscriptionRequestDTO request);

    SubscriptionCreationResponseDTO toDTO(Subscription savedEntity);

    Subscription toModel(SubscriptionRequestDTO requestDTO);
}
