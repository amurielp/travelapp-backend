package com.travelapp.web.dto.request;

import com.travelapp.events.domain.*;
import java.time.OffsetDateTime;

public record UpdateEventRequest(
    String              title,
    String              notes,
    String              color,
    OffsetDateTime      startDatetime,
    OffsetDateTime      endDatetime,
    String              status,
    String              locationName,
    Double              latitude,
    Double              longitude,
    FlightDetail        flight,
    AccommodationDetail accommodation,
    ActivityDetail      activity,
    TransportDetail     transport
) {}
