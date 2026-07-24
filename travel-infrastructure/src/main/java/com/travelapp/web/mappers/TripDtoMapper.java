package com.travelapp.web.mappers;

import com.travelapp.trips.domain.Trip;
import com.travelapp.trips.usecases.CreateTripCommand;
import com.travelapp.trips.usecases.UpdateTripCommand;
import com.travelapp.web.dto.request.CreateTripRequest;
import com.travelapp.web.dto.request.UpdateTripRequest;
import com.travelapp.web.dto.response.TripResponse;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface TripDtoMapper {

    @Mapping(target = "ownerId",      source = "userId")
    @Mapping(target = "baseCurrency", expression = "java(req.baseCurrency() != null ? req.baseCurrency() : \"EUR\")")
    CreateTripCommand toCommand(CreateTripRequest req, UUID userId);

    @Mapping(target = "tripId", source = "id")
    UpdateTripCommand toUpdateCommand(UpdateTripRequest req, UUID id);

    @Mapping(target = "status",   expression = "java(trip.getStatus().name())")
    @Mapping(target = "isPublic", source = "public")
    TripResponse toResponse(Trip trip);
}
