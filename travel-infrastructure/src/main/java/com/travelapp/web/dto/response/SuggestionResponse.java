package com.travelapp.web.dto.response;

import com.travelapp.ai.domain.Suggestion;
import java.util.UUID;

public record SuggestionResponse(
    UUID   id,
    String name,
    String category,
    String description,
    String reasoning,
    int    estimatedDurationMin,
    Double estimatedCostEur,
    String bestTimeOfDay,
    Double latitude,
    Double longitude,
    String websiteUrl,
    String source,
    String status
) {
    public static SuggestionResponse from(Suggestion s) {
        return new SuggestionResponse(
            s.getId(),
            s.getName(),
            s.getCategory(),
            s.getDescription(),
            s.getReasoning(),
            s.getEstimatedDurationMin(),
            s.getEstimatedCostEur(),
            s.getBestTimeOfDay(),
            s.getLatitude(),
            s.getLongitude(),
            s.getWebsiteUrl(),
            s.getSource(),
            s.getStatus().name().toLowerCase()
        );
    }
}
