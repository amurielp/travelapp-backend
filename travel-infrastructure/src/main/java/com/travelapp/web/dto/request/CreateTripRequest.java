package com.travelapp.web.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CreateTripRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 120)
    String title,

    String description,

    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate,

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be ISO 4217 (e.g. EUR)")
    String baseCurrency
) {}
