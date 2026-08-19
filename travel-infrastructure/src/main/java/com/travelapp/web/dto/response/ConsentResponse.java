package com.travelapp.web.dto.response;

import java.time.OffsetDateTime;

public record ConsentResponse(
    boolean adsPersonalized,
    boolean analytics,
    String consentVersion,
    OffsetDateTime consentedAt
) {}
