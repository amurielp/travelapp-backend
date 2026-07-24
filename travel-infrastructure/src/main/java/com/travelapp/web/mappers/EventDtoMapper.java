package com.travelapp.web.mappers;

import com.travelapp.events.domain.*;
import com.travelapp.events.usecases.*;
import com.travelapp.web.dto.request.CreateEventRequest;
import com.travelapp.web.dto.request.UpdateEventRequest;
import com.travelapp.web.dto.response.EventResponse;
import com.travelapp.web.dto.response.TripDaySummaryResponse;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventDtoMapper {

    @Mapping(target = "type",     expression = "java(event.getType().name())")
    @Mapping(target = "status",   expression = "java(event.getStatus().name())")
    @Mapping(target = "source",   expression = "java(event.getSource().name())")
    @Mapping(target = "timezone", expression = "java(event.getTimezone() != null ? event.getTimezone().getId() : null)")
    EventResponse toResponse(TravelEvent event);

    @Mapping(target = "tripId",      source = "tripId")
    @Mapping(target = "documentId",  ignore = true)
    @Mapping(target = "type",        expression = "java(EventType.valueOf(req.type()))")
    @Mapping(target = "timezone",    expression = "java(req.timezone() != null ? java.time.ZoneId.of(req.timezone()) : java.time.ZoneId.of(\"Europe/Madrid\"))")
    @Mapping(target = "source",      expression = "java(EventSource.MANUAL)")
    CreateEventCommand toCommand(CreateEventRequest req, UUID tripId);

    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "tripId",  source = "tripId")
    @Mapping(target = "status",  expression = "java(req.status() != null ? EventStatus.valueOf(req.status()) : null)")
    UpdateEventCommand toUpdateCommand(UpdateEventRequest req, UUID eventId, UUID tripId);

    @Mapping(target = "freeSlots", source = "freeSlots")
    TripDaySummaryResponse toDaySummaryResponse(TripDaySummary summary);

    default TripDaySummaryResponse.FreeSlotDto toFreeSlotDto(FreeSlot slot) {
        return new TripDaySummaryResponse.FreeSlotDto(
            slot.from().toString(), slot.to().toString());
    }
}
