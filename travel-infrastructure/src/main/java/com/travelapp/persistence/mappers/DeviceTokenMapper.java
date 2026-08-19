package com.travelapp.persistence.mappers;

import com.travelapp.notifications.domain.DeviceToken;
import com.travelapp.persistence.entities.DeviceTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeviceTokenMapper {

    DeviceTokenEntity toEntity(DeviceToken token);

    DeviceToken toDomain(DeviceTokenEntity entity);
}
