package com.travelapp.ai.domain;
import java.util.List;
import java.util.Map;
public record ParsedDocument(DocumentType type, Map<String, Object> fields, double confidence, List<String> missingFields) {
    public boolean needsManualReview() { return confidence < 0.8 || !missingFields.isEmpty(); }
    public static ParsedDocument unknown(String reason) {
        return new ParsedDocument(DocumentType.UNKNOWN, Map.of("error", reason), 0.0, List.of());
    }
}
