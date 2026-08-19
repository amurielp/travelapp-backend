package com.travelapp.web.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public record UserFeaturesResponse(
    boolean showAds,
    boolean aiSuggestions,
    boolean aiPdfParsing,
    boolean multiTraveler,
    String plan,
    OffsetDateTime planExpiresAt,
    List<RewardedUnlockResponse> rewardedUnlocks
) {}
