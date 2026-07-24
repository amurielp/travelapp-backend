package com.travelapp.events.domain;

import lombok.Builder;
import java.time.LocalDate;
import java.util.List;

@Builder
public record TripDaySummary(
    LocalDate    date,
    String       destinationCity,
    Double       latitude,
    Double       longitude,
    String       occupancy,     // free | partial | full
    List<FreeSlot> freeSlots,
    int          eventCount
) {}
