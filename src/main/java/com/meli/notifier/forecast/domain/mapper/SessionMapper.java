package com.meli.notifier.forecast.domain.mapper;

import com.meli.notifier.forecast.adapter.persistence.entity.SessionEntity;
import com.meli.notifier.forecast.domain.model.database.Session;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class})
public interface SessionMapper {

    SessionMapper INSTANCE = Mappers.getMapper(SessionMapper.class);

    Session toModel(SessionEntity entity);

    SessionEntity toEntity(Session model);
}
