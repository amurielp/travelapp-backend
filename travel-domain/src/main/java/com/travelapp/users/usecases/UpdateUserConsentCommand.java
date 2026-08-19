package com.travelapp.users.usecases;

import java.util.UUID;

public record UpdateUserConsentCommand(
    UUID userId,
    boolean adsPersonalized,
    boolean analytics,
    String consentVersion
) {}
