package com.travelapp.web.dto.request;

import java.time.LocalDate;

public record UpdateTripRequest(
    String    title,
    String    description,
    LocalDate startDate,
    LocalDate endDate,
    String    baseCurrency,
    String    coverImageUrl
) {}
