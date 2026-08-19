package com.travelapp.web.controllers;

import com.travelapp.trips.usecases.*;
import com.travelapp.web.dto.request.*;
import com.travelapp.web.dto.response.ShareTripResponse;
import com.travelapp.web.dto.response.TripResponse;
import com.travelapp.web.mappers.TripDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

    private final CreateTripUseCase         createTrip;
    private final GetUserTripsUseCase       getUserTrips;
    private final GetTripUseCase            getTrip;
    private final UpdateTripUseCase         updateTrip;
    private final DeleteTripUseCase         deleteTrip;
    private final PublishTripUseCase        publishTrip;
    private final ValidateTripAccessUseCase validateAccess;
    private final ShareTripUseCase          shareTrip;
    private final TripDtoMapper             mapper;

    @PostMapping
    public ResponseEntity<TripResponse> create(
            @Valid @RequestBody CreateTripRequest req, @AuthenticationPrincipal Jwt jwt) {
        var trip = createTrip.execute(mapper.toCommand(req, userId(jwt)));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(trip));
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> list(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(getUserTrips.execute(userId(jwt))
            .stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getById(
            @PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(id, userId(jwt));
        return ResponseEntity.ok(mapper.toResponse(getTrip.execute(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TripResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTripRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(id, userId(jwt));
        return ResponseEntity.ok(mapper.toResponse(updateTrip.execute(mapper.toUpdateCommand(req, id))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(id, userId(jwt));
        deleteTrip.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<TripResponse> publish(
            @PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(id, userId(jwt));
        return ResponseEntity.ok(mapper.toResponse(publishTrip.execute(id)));
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<ShareTripResponse> share(
            @PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(id, userId(jwt));
        var result = shareTrip.execute(id);
        return ResponseEntity.ok(new ShareTripResponse(result.deepLink(), result.webUrl(), result.expiresAt()));
    }

    private UUID userId(Jwt jwt) { return UUID.fromString(jwt.getSubject()); }
}
