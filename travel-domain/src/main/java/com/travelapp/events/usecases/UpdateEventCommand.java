package com.travelapp.events.usecases;

import com.travelapp.events.domain.*;
import java.time.*;
import java.util.UUID;

public record UpdateEventCommand(
    UUID           eventId,
    UUID           tripId,
    String         title,
    String         notes,
    String         color,
    OffsetDateTime startDatetime,
    OffsetDateTime endDatetime,
    EventStatus    status,
    String         locationName,
    Double         latitude,
    Double         longitude,
    FlightDetail        flight,
    AccommodationDetail accommodation,
    ActivityDetail      activity,
    TransportDetail     transport,
    EsimDetail          esim,
    InsuranceDetail     insurance
) {}
