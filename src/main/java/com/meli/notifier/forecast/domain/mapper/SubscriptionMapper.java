package com.meli.notifier.forecast.domain.mapper;

import com.meli.notifier.forecast.adapter.persistence.entity.SubscriptionEntity;
import com.meli.notifier.forecast.application.dto.request.SubscriptionRequestDTO;
import com.meli.notifier.forecast.domain.model.database.Subscription;
import com.meli.notifier.forecast.domain.model.database.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class, CityMapper.class})
public interface SubscriptionMapper {

    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);

    Subscription toModel(SubscriptionEntity entity);

    SubscriptionEntity toEntity(Subscription model);

    @Mapping(target = "user", source = "user")
    SubscriptionEntity toEntity(User user, SubscriptionRequestDTO request);

}
