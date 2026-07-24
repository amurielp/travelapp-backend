package com.travelapp.web.dto.request;

import java.util.List;

public record UserPreferencesRequest(
    List<String> interests,
    String       foodProfile,
    String       travelStyle,
    String       budgetLevel,
    String       language,
    String       currency
) {}
