package com.travelapp.adapters.export;

import com.travelapp.export.ports.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class WeasyPrintExporter implements ItineraryExporter {

    private final WebClient aiClient;

    public WeasyPrintExporter(@Qualifier("aiServiceWebClient") WebClient aiClient) {
        this.aiClient = aiClient;
    }

    @Override
    public CompletableFuture<String> exportToPdf(
            java.util.UUID tripId,
            java.util.UUID userId,
            ExportOptions options) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("export.pdf.start tripId={}", tripId);
                var body = Map.of(
                    "trip_id",         tripId.toString(),
                    "user_id",         userId.toString(),
                    "language",        options.language(),
                    "include_budget",  options.includeBudget(),
                    "include_map",     options.includeMap(),
                    "export_type",     options.exportType()
                );
                var result = aiClient.post()
                    .uri("/export/pdf")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

                var url = result != null ? (String) result.get("pdf_url") : null;
                log.info("export.pdf.done tripId={} url={}", tripId, url);
                return url;
            } catch (Exception e) {
                log.error("export.pdf.failed tripId={}", tripId, e);
                throw new RuntimeException("Export failed", e);
            }
        });
    }
}
