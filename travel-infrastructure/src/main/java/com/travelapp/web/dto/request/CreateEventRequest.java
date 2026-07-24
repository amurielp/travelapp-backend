package com.travelapp.web.dto.request;

import com.travelapp.events.domain.*;
import java.time.OffsetDateTime;

public record CreateEventRequest(
    String              type,
    String              title,
    String              notes,
    String              color,
    OffsetDateTime      startDatetime,
    OffsetDateTime      endDatetime,
    boolean             allDay,
    String              timezone,
    String              locationName,
    Double              latitude,
    Double              longitude,
    FlightDetail        flight,
    AccommodationDetail accommodation,
    ActivityDetail      activity,
    TransportDetail     transport
) {}
