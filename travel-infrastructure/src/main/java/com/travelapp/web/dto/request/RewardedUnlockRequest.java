package com.travelapp.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RewardedUnlockRequest(
    @NotBlank String featureKey,
    @NotNull Integer ttlHours
) {}
