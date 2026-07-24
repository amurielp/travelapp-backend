package com.travelapp.export.ports;

public record ExportOptions(
    String language,         // "es" | "en"
    boolean includeBudget,   // incluir sección de presupuesto
    boolean includeMap,      // incluir mapa del itinerario
    boolean includeDocuments,// incluir lista de documentos
    String exportType        // "full_itinerary" | "day_by_day" | "payment_report"
) {
    public static ExportOptions defaults() {
        return new ExportOptions("es", false, true, true, "full_itinerary");
    }
}
