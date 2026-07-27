package com.travelapp.persistence.mappers;

import com.travelapp.events.domain.*;
import com.travelapp.persistence.entities.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface DetailEntityMapper {

    @Mapping(target = "isConnection", ignore = true)
    FlightDetail toDomain(FlightEntity e);

    @Mapping(target = "id",    ignore = true)
    @Mapping(target = "event", ignore = true)
    FlightEntity toEntity(FlightDetail d);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",    ignore = true)
    @Mapping(target = "event", ignore = true)
    void updateFlight(FlightDetail d, @MappingTarget FlightEntity e);

    // ── Accommodation ─────────────────────────────────────────────

    AccommodationDetail toDomain(AccommodationEntity e);

    @Mapping(target = "id",    ignore = true)
    @Mapping(target = "event", ignore = true)
    AccommodationEntity toEntity(AccommodationDetail d);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",    ignore = true)
    @Mapping(target = "event", ignore = true)
    void updateAccommodation(AccommodationDetail d, @MappingTarget AccommodationEntity e);

    // ── Activity ──────────────────────────────────────────────────

    ActivityDetail toDomain(ActivityEntity e);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "event",     ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ActivityEntity toEntity(ActivityDetail d);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "event",     ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateActivity(ActivityDetail d, @MappingTarget ActivityEntity e);

    // ── Transport ─────────────────────────────────────────────────

    TransportDetail toDomain(TransportEntity e);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "event",     ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TransportEntity toEntity(TransportDetail d);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "event",     ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateTransport(TransportDetail d, @MappingTarget TransportEntity e);
}
