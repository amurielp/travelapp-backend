package com.travelapp.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreatePaymentMethodRequest(
    @NotBlank String name,
    @NotBlank String type,
    String notes
) {}
