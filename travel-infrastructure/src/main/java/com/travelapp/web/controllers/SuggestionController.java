package com.travelapp.web.controllers;

import com.travelapp.ai.usecases.*;
import com.travelapp.trips.usecases.ValidateTripAccessUseCase;
import com.travelapp.web.dto.response.EventResponse;
import com.travelapp.web.dto.response.SuggestionResponse;
import com.travelapp.web.mappers.EventDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/trips/{tripId}/suggestions")
@RequiredArgsConstructor
public class SuggestionController {

    private final GetSuggestionsUseCase     getSuggestions;
    private final AcceptSuggestionUseCase   acceptSuggestion;
    private final DismissSuggestionUseCase  dismissSuggestion;
    private final ValidateTripAccessUseCase validateAccess;
    private final EventDtoMapper            eventMapper;

    @GetMapping
    public ResponseEntity<List<SuggestionResponse>> list(
            @PathVariable UUID tripId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, UUID.fromString(jwt.getSubject()));
        var suggestions = getSuggestions.execute(tripId, date, jwt.getSubject())
            .stream().map(SuggestionResponse::from).toList();
        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/{suggestionId}/accept")
    public ResponseEntity<EventResponse> accept(
            @PathVariable UUID tripId,
            @PathVariable UUID suggestionId,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, UUID.fromString(jwt.getSubject()));
        var event = acceptSuggestion.execute(suggestionId, tripId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventMapper.toResponse(event));
    }

    @PostMapping("/{suggestionId}/dismiss")
    public ResponseEntity<Void> dismiss(
            @PathVariable UUID tripId,
            @PathVariable UUID suggestionId,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, UUID.fromString(jwt.getSubject()));
        dismissSuggestion.execute(suggestionId);
        return ResponseEntity.noContent().build();
    }
}
