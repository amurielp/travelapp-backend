package com.travelapp.web.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record TripResponse(
    UUID      id,
    String    title,
    String    description,
    String    status,
    LocalDate startDate,
    LocalDate endDate,
    String    baseCurrency,
    boolean   isPublic,
    String    publicSlug,
    String    coverImageUrl
) {}
