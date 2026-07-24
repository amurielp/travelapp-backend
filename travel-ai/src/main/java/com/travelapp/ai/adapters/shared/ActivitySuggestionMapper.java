package com.travelapp.ai.adapters.shared;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelapp.ai.domain.ActivitySuggestion;

import java.util.List;
import java.util.Map;

public final class ActivitySuggestionMapper {
    private static final ObjectMapper JSON = new ObjectMapper();
    private ActivitySuggestionMapper() {}

    public static List<ActivitySuggestion> fromJson(String raw) {
        try {
            var clean = raw.replaceAll("```(?:json)?", "").trim();
            List<Map<String, Object>> list = JSON.readValue(clean, new TypeReference<>() {});
            return list.stream().map(m -> new ActivitySuggestion(
                (String) m.get("name"),
                (String) m.getOrDefault("category", "other"),
                (String) m.getOrDefault("description", ""),
                (String) m.getOrDefault("reasoning", ""),
                m.containsKey("estimated_duration_min")
                    ? ((Number) m.get("estimated_duration_min")).intValue() : 60,
                m.containsKey("estimated_cost_eur") && m.get("estimated_cost_eur") != null
                    ? ((Number) m.get("estimated_cost_eur")).doubleValue() : null,
                (String) m.getOrDefault("best_time_of_day", "any"),
                m.containsKey("latitude")  && m.get("latitude")  != null
                    ? ((Number) m.get("latitude")).doubleValue()  : null,
                m.containsKey("longitude") && m.get("longitude") != null
                    ? ((Number) m.get("longitude")).doubleValue() : null,
                null,
                (String) m.get("website_url"),
                "ai_generated"
            )).toList();
        } catch (Exception e) {
            return List.of();
        }
    }
}
