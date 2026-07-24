package com.travelapp.ai.domain;
public record ActivitySuggestion(
    String name, String category, String description, String reasoning,
    int estimatedDurationMin, Double estimatedCostEur, String bestTimeOfDay,
    Double latitude, Double longitude, String externalPlaceId, String websiteUrl, String source) {}
