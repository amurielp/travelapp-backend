package com.travelapp.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateConsentRequest(
    @NotNull Boolean adsPersonalized,
    @NotNull Boolean analytics,
    @NotBlank String consentVersion
) {}
