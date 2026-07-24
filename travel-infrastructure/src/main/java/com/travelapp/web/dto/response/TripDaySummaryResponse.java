package com.travelapp.web.dto.response;

import java.time.LocalDate;
import java.util.List;

public record TripDaySummaryResponse(
    LocalDate        date,
    String           destinationCity,
    Double           latitude,
    Double           longitude,
    String           occupancy,
    List<FreeSlotDto> freeSlots,
    int              eventCount
) {
    public record FreeSlotDto(String from, String to) {}
}
