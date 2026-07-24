package com.travelapp.ai.adapters.shared;

import com.travelapp.ai.domain.SuggestionRequest;

/**
 * Biblioteca de prompts del sistema.
 * Fuente de verdad — el ai-service Python tiene la misma lógica en prompts/library.py.
 * Cualquier cambio aquí debe replicarse allí.
 */
public final class PromptLibrary {
    private PromptLibrary() {}

    public static final String PDF_PARSER_SYSTEM = """
        Eres un extractor de datos de documentos de viaje.
        Devuelve SOLO un objeto JSON válido, sin texto adicional, sin backticks.
        Estructura: {
          "type": "FLIGHT|ACCOMMODATION|CAR_RENTAL|ACTIVITY_TICKET|UNKNOWN",
          "confidence": 0.0-1.0,
          "fields": { ... campos específicos ... },
          "missing_fields": [ ... campos que no aparecen ... ]
        }
        Para FLIGHT: airline, flight_number, origin_iata, destination_iata,
                     departure_at (ISO8601), arrival_at, seat, cabin_class, booking_ref.
        Para ACCOMMODATION: name, city, check_in_date, check_out_date,
                            check_in_time, check_out_time, booking_ref.
        Si un campo no aparece en el documento: null y añadirlo a missing_fields.""";

    public static String pdfParserUser(String rawText) {
        return "Texto extraído del documento:\n\n" + rawText;
    }

    public static final String SUGGESTIONS_SYSTEM = """
        Eres un asistente de viajes personal. Devuelve SOLO un array JSON, sin texto ni backticks.
        Máximo 3 sugerencias. Cada elemento:
        {
          "name": "Nombre del lugar o actividad",
          "category": "gastronomy|museum|nature|shopping|nightlife|sport|local_markets|other",
          "description": "1-2 frases. Concreto y útil, no genérico.",
          "reasoning": "Por qué encaja con los intereses y el hueco disponible.",
          "estimated_duration_min": número,
          "estimated_cost_eur": número o null si es gratuito,
          "best_time_of_day": "morning|afternoon|evening|any",
          "latitude": número o null,
          "longitude": número o null,
          "website_url": "url" o null
        }""";

    public static String suggestionsUser(SuggestionRequest req) {
        var slots = req.freeSlots().stream()
            .map(s -> s.from() + "–" + s.to())
            .toList();
        return """
            Destino: %s (%s)
            Fecha: %s
            Huecos disponibles: %s
            Intereses del usuario: %s
            Perfil gastronómico: %s
            Ritmo de viaje: %s, evitar multitudes: %s
            Ya en wishlist (no repetir): %s
            """.formatted(
                req.destinationCity(), req.countryCode(),
                req.date(), slots,
                req.userInterests(),
                req.foodProfile() != null ? req.foodProfile().level() : "normal",
                req.travelStyle() != null ? req.travelStyle().pace() : "moderate",
                req.travelStyle() != null && req.travelStyle().avoidCrowds(),
                req.alreadyInWishlist()
        );
    }
}
