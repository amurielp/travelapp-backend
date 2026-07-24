package com.travelapp.web.dto.response;

import com.travelapp.events.domain.*;
import java.time.OffsetDateTime;
import java.util.UUID;

public record EventResponse(
    UUID                id,
    UUID                tripId,
    UUID                documentId,
    String              type,
    String              title,
    String              notes,
    String              color,
    OffsetDateTime      startDatetime,
    OffsetDateTime      endDatetime,
    boolean             allDay,
    String              timezone,
    String              status,
    String              source,
    String              locationName,
    Double              latitude,
    Double              longitude,
    FlightDetail        flight,
    AccommodationDetail accommodation,
    ActivityDetail      activity,
    TransportDetail     transport
) {}
