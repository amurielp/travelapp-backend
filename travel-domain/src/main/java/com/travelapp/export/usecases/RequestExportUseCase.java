package com.travelapp.export.usecases;

import com.travelapp.export.ports.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j @Service @RequiredArgsConstructor
public class RequestExportUseCase {

    private final ItineraryExporter exporter;

    public void execute(UUID tripId, UUID userId, ExportOptions options) {
        log.info("export.requested tripId={} userId={} type={}", tripId, userId, options.exportType());
        exporter.exportToPdf(tripId, userId, options)
            .thenAccept(url -> log.info("export.done tripId={} url={}", tripId, url))
            .exceptionally(e -> { log.error("export.failed tripId={}", tripId, e); return null; });
    }
}
