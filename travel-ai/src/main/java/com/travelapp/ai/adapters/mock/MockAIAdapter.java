package com.travelapp.ai.adapters.mock;

import com.travelapp.ai.domain.*;
import com.travelapp.ai.ports.AIProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Adaptador mock — activo cuando ai.provider=mock o la propiedad no está definida.
 * Usado en tests y CI. No hace ninguna llamada de red.
 * matchIfMissing=true garantiza que siempre hay al menos un AIProvider disponible.
 */
@Component
@ConditionalOnProperty(
    name         = "ai.provider",
    havingValue  = "mock",
    matchIfMissing = true          // ← si la propiedad no existe, este bean se registra
)
public class MockAIAdapter implements AIProvider {

    @Override
    public AIResponse complete(AIRequest request) {
        return new AIResponse(
            "{\"mock\":true}",
            new TokenUsage(10, 10, 0.0),
            providerName(),
            false
        );
    }

    @Override
    public ParsedDocument parseTravelDocument(String rawText) {
        return new ParsedDocument(
            DocumentType.FLIGHT,
            Map.of(
                "airline",          "Vueling",
                "flight_number",    "VY1234",
                "origin_iata",      "BCN",
                "destination_iata", "MAD",
                "departure_at",     "2025-08-15T06:30:00Z",
                "arrival_at",       "2025-08-15T07:45:00Z",
                "booking_ref",      "MOCK01"
            ),
            0.99,
            List.of()
        );
    }

    @Override
    public List<ActivitySuggestion> suggestActivities(SuggestionRequest request) {
        return List.of(
            new ActivitySuggestion(
                "Mercado de la Boqueria",
                "local_markets",
                "Mercado icónico de Barcelona.",
                "Encaja con tu interés en gastronomía local.",
                90, 0.0, "morning",
                41.3817, 2.1717,
                "mock-boqueria-001",
                "https://www.boqueria.barcelona",
                "mock"
            )
        );
    }

    @Override
    public String providerName() { return "mock"; }
}
