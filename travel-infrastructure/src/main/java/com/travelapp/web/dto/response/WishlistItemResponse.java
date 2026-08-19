package com.travelapp.web.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record WishlistItemResponse(
    UUID       id,
    String     name,
    String     category,
    String     destinationCity,
    Double     latitude,
    Double     longitude,
    int        priority,
    String     notes,
    BigDecimal estimatedCost,
    String     websiteUrl,
    UUID       convertedToEventId
) {}
