package com.travelapp.persistence.mappers;

import com.travelapp.trips.domain.*;
import com.travelapp.persistence.entities.TripEntity;
import com.travelapp.persistence.entities.TripStatusEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TripMapper {

    @Mapping(target = "status",    expression = "java(com.travelapp.persistence.entities.TripStatusEntity.valueOf(trip.getStatus().name()))")
    @Mapping(target = "isPublic",  source = "public")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TripEntity toEntity(Trip trip);

    @Mapping(target = "status",   expression = "java(com.travelapp.trips.domain.TripStatus.valueOf(entity.getStatus().name()))")
    @Mapping(target = "isPublic", source = "public")
    Trip toDomain(TripEntity entity);
}
