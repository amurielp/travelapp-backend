package com.travelapp.persistence.mappers;

import com.travelapp.events.domain.*;
import com.travelapp.persistence.entities.EventEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "type",     expression = "java(event.getType().name())")
    @Mapping(target = "status",   expression = "java(event.getStatus().name())")
    @Mapping(target = "source",   expression = "java(event.getSource().name())")
    @Mapping(target = "timezone", expression = "java(event.getTimezone().getId())")
    EventEntity toEntity(TravelEvent event);

    @Mapping(target = "type",          expression = "java(com.travelapp.events.domain.EventType.valueOf(entity.getType()))")
    @Mapping(target = "status",        expression = "java(com.travelapp.events.domain.EventStatus.valueOf(entity.getStatus()))")
    @Mapping(target = "source",        expression = "java(com.travelapp.events.domain.EventSource.valueOf(entity.getSource()))")
    @Mapping(target = "timezone",      expression = "java(java.time.ZoneId.of(entity.getTimezone()))")
    @Mapping(target = "flight",        ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "activity",      ignore = true)
    @Mapping(target = "transport",     ignore = true)
    TravelEvent toDomain(EventEntity entity);
}
