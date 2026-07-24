package com.travelapp.ai.adapters.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelapp.ai.domain.*;

import java.util.List;
import java.util.Map;

public final class ParsedDocumentMapper {
    private static final ObjectMapper JSON = new ObjectMapper();
    private ParsedDocumentMapper() {}

    @SuppressWarnings("unchecked")
    public static ParsedDocument fromJson(String raw) {
        try {
            var clean  = raw.replaceAll("```(?:json)?", "").trim();
            var map    = JSON.readValue(clean, Map.class);
            var type   = DocumentType.valueOf(
                ((String) map.getOrDefault("type", "UNKNOWN")).toUpperCase());
            var conf   = ((Number) map.getOrDefault("confidence", 0.0)).doubleValue();
            var fields = (Map<String, Object>) map.getOrDefault("fields", Map.of());
            var missing = (List<String>) map.getOrDefault("missing_fields", List.of());
            return new ParsedDocument(type, fields, conf, missing);
        } catch (Exception e) {
            return ParsedDocument.unknown("Parse error: " + e.getMessage());
        }
    }
}
