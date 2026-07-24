package com.travelapp.events.usecases;

import com.travelapp.events.domain.*;
import java.time.*;
import java.util.UUID;

public record CreateEventCommand(
    UUID            tripId,
    UUID            documentId,   // null si es manual
    EventType       type,
    String          title,
    String          notes,
    String          color,
    OffsetDateTime  startDatetime,
    OffsetDateTime  endDatetime,
    boolean         allDay,
    ZoneId          timezone,
    EventSource     source,
    String          locationName,
    Double          latitude,
    Double          longitude,
    FlightDetail        flight,
    AccommodationDetail accommodation,
    ActivityDetail      activity,
    TransportDetail     transport
) {}
