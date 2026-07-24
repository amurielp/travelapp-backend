package com.travelapp.persistence.mappers;

import com.travelapp.gaps.domain.*;
import com.travelapp.persistence.entities.TripGapEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TripGapMapper {

    @Mapping(target = "gapType",  expression = "java(gap.getGapType().name())")
    @Mapping(target = "severity", expression = "java(gap.getSeverity().name())")
    @Mapping(target = "status",   expression = "java(gap.getStatus().name())")
    TripGapEntity toEntity(TripGap gap);

    @Mapping(target = "gapType",  expression = "java(com.travelapp.gaps.domain.GapType.valueOf(entity.getGapType()))")
    @Mapping(target = "severity", expression = "java(com.travelapp.gaps.domain.GapSeverity.valueOf(entity.getSeverity()))")
    @Mapping(target = "status",   expression = "java(com.travelapp.gaps.domain.GapStatus.valueOf(entity.getStatus()))")
    TripGap toDomain(TripGapEntity entity);
}
