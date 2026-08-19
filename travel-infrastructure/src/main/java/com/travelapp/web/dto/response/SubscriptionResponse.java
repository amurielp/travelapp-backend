package com.travelapp.web.dto.response;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record SubscriptionResponse(
        String planId,
        String status,
        String store,
        boolean autoRenew,
        OffsetDateTime startedAt,
        OffsetDateTime expiresAt,
        LocalDate trialEnd
) {}
