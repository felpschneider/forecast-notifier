package com.meli.notifier.forecast.domain.mapper;

import com.meli.notifier.forecast.adapter.persistence.entity.UserEntity;
import com.meli.notifier.forecast.application.dto.request.RegisterRequestDTO;
import com.meli.notifier.forecast.domain.model.database.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toModel(UserEntity entity);

    @Mapping(target = "passwordHash", source = "password")
    User toModel(RegisterRequestDTO registerRequestDTO);

    UserEntity toEntity(User model);


}
