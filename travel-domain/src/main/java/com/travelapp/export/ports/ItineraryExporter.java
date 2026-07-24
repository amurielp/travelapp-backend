package com.travelapp.export.ports;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Puerto de exportación de itinerarios.
 * Implementaciones: WeasyPrintExporter (Python/PDF), JasperExporter (Java/PDF)
 */
public interface ItineraryExporter {
    /** Genera el PDF de forma asíncrona y devuelve la URL del resultado */
    CompletableFuture<String> exportToPdf(UUID tripId, UUID userId, ExportOptions options);
}
