package com.travelapp.web.controllers;

import com.travelapp.export.ports.ExportOptions;
import com.travelapp.export.usecases.RequestExportUseCase;
import com.travelapp.trips.usecases.ValidateTripAccessUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/trips/{tripId}/export")
@RequiredArgsConstructor
public class ExportController {

    private final RequestExportUseCase  exporter;
    private final ValidateTripAccessUseCase validateAccess;

    @PostMapping
    public ResponseEntity<Map<String, String>> requestExport(
            @PathVariable UUID tripId,
            @RequestBody(required = false) ExportOptions options,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        validateAccess.execute(tripId, userId);
        var opts = options != null ? options : ExportOptions.defaults();
        exporter.execute(tripId, userId, opts);
        return ResponseEntity.accepted()
            .body(Map.of("message", "Export requested. You'll receive a notification when ready."));
    }
}
