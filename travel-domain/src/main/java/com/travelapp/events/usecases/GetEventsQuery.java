package com.travelapp.events.usecases;

import lombok.Builder;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record GetEventsQuery(
    UUID      tripId,
    String    type,
    LocalDate from,
    LocalDate to,
    String    status
) {}
