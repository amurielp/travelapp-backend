package com.travelapp.web.controllers;

import com.travelapp.gaps.domain.*;
import com.travelapp.gaps.ports.TripGapRepository;
import com.travelapp.gaps.usecases.*;
import com.travelapp.trips.usecases.ValidateTripAccessUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/trips/{tripId}/gaps")
@RequiredArgsConstructor
public class GapController {

    private final DetectTripGapsUseCase detector;
    private final ResolveGapUseCase     resolver;
    private final TripGapRepository     gapRepo;
    private final ValidateTripAccessUseCase validateAccess;

    /** Lista los huecos abiertos del viaje */
    @GetMapping
    public ResponseEntity<List<TripGap>> listGaps(
            @PathVariable UUID tripId,
            @RequestParam(defaultValue = "true") boolean onlyOpen,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, UUID.fromString(jwt.getSubject()));
        var gaps = onlyOpen
            ? gapRepo.findOpenByTripId(tripId)
            : gapRepo.findAllByTripId(tripId);
        return ResponseEntity.ok(gaps);
    }

    /** Recalcula huecos manualmente */
    @PostMapping("/recalculate")
    public ResponseEntity<List<TripGap>> recalculate(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(detector.execute(tripId));
    }

    /** Ignorar un hueco con motivo */
    @PostMapping("/{gapId}/ignore")
    public ResponseEntity<TripGap> ignore(
            @PathVariable UUID tripId,
            @PathVariable UUID gapId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(resolver.ignore(gapId, body.getOrDefault("reason", "")));
    }

    /** Posponer alerta del hueco */
    @PostMapping("/{gapId}/snooze")
    public ResponseEntity<TripGap> snooze(
            @PathVariable UUID tripId,
            @PathVariable UUID gapId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, UUID.fromString(jwt.getSubject()));
        var until = OffsetDateTime.parse(body.getOrDefault("until",
            OffsetDateTime.now().plusDays(7).toString()));
        return ResponseEntity.ok(resolver.snooze(gapId, until));
    }
}
