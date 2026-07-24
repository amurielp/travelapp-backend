package com.travelapp.web.controllers;

import com.travelapp.events.usecases.*;
import com.travelapp.trips.usecases.ValidateTripAccessUseCase;
import com.travelapp.web.dto.request.CreateEventRequest;
import com.travelapp.web.dto.request.UpdateEventRequest;
import com.travelapp.web.dto.response.EventResponse;
import com.travelapp.web.dto.response.TripDaySummaryResponse;
import com.travelapp.web.mappers.EventDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/trips/{tripId}/events")
@RequiredArgsConstructor
public class EventController {

    private final CreateEventUseCase        createEvent;
    private final GetEventsUseCase          getEvents;
    private final GetEventUseCase           getEvent;
    private final GetTripDaysUseCase        getTripDays;
    private final UpdateEventUseCase        updateEvent;
    private final DeleteEventUseCase        deleteEvent;
    private final ValidateTripAccessUseCase validateAccess;
    private final EventDtoMapper            mapper;

    @PostMapping
    public ResponseEntity<EventResponse> create(
            @PathVariable UUID tripId,
            @Valid @RequestBody CreateEventRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, userId(jwt));
        var event = createEvent.execute(mapper.toCommand(req, tripId));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(event));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> list(
            @PathVariable UUID tripId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String type,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, userId(jwt));
        var query = new GetEventsQuery(tripId, type,
            from != null ? LocalDate.parse(from) : null,
            to   != null ? LocalDate.parse(to)   : null,
            null);
        return ResponseEntity.ok(getEvents.execute(query)
            .stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getById(
            @PathVariable UUID tripId,
            @PathVariable UUID eventId,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, userId(jwt));
        return ResponseEntity.ok(mapper.toResponse(getEvent.execute(eventId, tripId)));
    }

    @GetMapping("/by-day")
    public ResponseEntity<Map<String, List<EventResponse>>> byDay(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, userId(jwt));
        var events = getEvents.execute(new GetEventsQuery(tripId, null, null, null, null));
        var result = new LinkedHashMap<String, List<EventResponse>>();
        events.stream()
            .filter(e -> e.getStartDatetime() != null)
            .forEach(e -> result
                .computeIfAbsent(e.getStartDatetime().toLocalDate().toString(), k -> new ArrayList<>())
                .add(mapper.toResponse(e)));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/days")
    public ResponseEntity<List<TripDaySummaryResponse>> days(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, userId(jwt));
        return ResponseEntity.ok(getTripDays.execute(tripId)
            .stream().map(mapper::toDaySummaryResponse).toList());
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventResponse> update(
            @PathVariable UUID tripId,
            @PathVariable UUID eventId,
            @Valid @RequestBody UpdateEventRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, userId(jwt));
        var event = updateEvent.execute(mapper.toUpdateCommand(req, eventId, tripId));
        return ResponseEntity.ok(mapper.toResponse(event));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID tripId,
            @PathVariable UUID eventId,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, userId(jwt));
        deleteEvent.execute(eventId, tripId);
        return ResponseEntity.noContent().build();
    }

    private UUID userId(Jwt jwt) { return UUID.fromString(jwt.getSubject()); }
}
