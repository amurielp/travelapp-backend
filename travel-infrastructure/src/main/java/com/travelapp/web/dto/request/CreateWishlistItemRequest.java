package com.travelapp.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CreateWishlistItemRequest(
    @NotBlank(message = "El nombre del ítem es obligatorio")
    String name,
    String category,
    String destinationCity,
    Double latitude,
    Double longitude,
    String externalPlaceId,
    int priority,
    BigDecimal estimatedCost,
    String websiteUrl
) {}
