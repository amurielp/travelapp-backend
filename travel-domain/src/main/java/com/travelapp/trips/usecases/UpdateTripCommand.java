package com.travelapp.trips.usecases;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateTripCommand(
    UUID       tripId,
    String     title,
    String     description,
    LocalDate  startDate,
    LocalDate  endDate,
    String     baseCurrency,
    String     coverImageUrl
) {}
