package com.travelapp.users.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserConsent {
    private UUID userId;
    private boolean adsPersonalized;
    private boolean analytics;
    private String consentVersion;
    private OffsetDateTime consentedAt;
    private OffsetDateTime updatedAt;
}
