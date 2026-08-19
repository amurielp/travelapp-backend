package com.travelapp.users.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
public class UserFeatures {
    private boolean showAds;
    private boolean aiSuggestions;
    private boolean aiPdfParsing;
    private boolean multiTraveler;
    private String plan;
    private OffsetDateTime planExpiresAt;
    private List<RewardedUnlock> rewardedUnlocks;
}
