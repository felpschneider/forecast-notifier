package com.meli.notifier.forecast.domain.mapper;

import com.meli.notifier.forecast.domain.entity.NotificationChannelEntity;
import com.meli.notifier.forecast.domain.model.database.NotificationChannel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class})
public interface NotificationChannelMapper {

    NotificationChannelMapper INSTANCE = Mappers.getMapper(NotificationChannelMapper.class);

    NotificationChannel toModel(NotificationChannelEntity entity);

    NotificationChannelEntity toEntity(NotificationChannel model);
}
