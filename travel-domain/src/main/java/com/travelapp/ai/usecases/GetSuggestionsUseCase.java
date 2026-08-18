package com.travelapp.ai.usecases;

import com.travelapp.ai.domain.*;
import com.travelapp.ai.ports.*;
import com.travelapp.events.domain.TripDaySummary;
import com.travelapp.events.usecases.GetTripDaysUseCase;
import com.travelapp.trips.usecases.ValidateTripAccessUseCase;
import com.travelapp.users.ports.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GetSuggestionsUseCase {

    private final AIProvider           aiProvider;
    private final SuggestionRepository suggestionRepository;
    private final GetTripDaysUseCase   tripDays;
    private final UserRepository       users;

    @Transactional
    public List<Suggestion> execute(UUID tripId, LocalDate date, String keycloakId) {
        var existing = suggestionRepository.findByTripIdAndDate(tripId, date).stream()
            .filter(s -> s.getStatus() == SuggestionStatus.PENDING)
            .toList();
        if (!existing.isEmpty()) return existing;

        var days = tripDays.execute(tripId);
        var dayOpt = days.stream()
            .filter(d -> d.date().equals(date))
            .findFirst();

        var destinationCity = dayOpt.map(TripDaySummary::destinationCity).orElse(null);
        var freeSlots = dayOpt.map(TripDaySummary::freeSlots).orElse(List.of());

        var user = users.findByKeycloakId(keycloakId).orElse(null);
        var prefs = user != null ? user.getPreferences() : null;

        var request = new SuggestionRequest(
            destinationCity, null, date, freeSlots,
            prefs != null ? prefs.interests() : List.of(),
            prefs != null ? prefs.foodProfile() : null,
            prefs != null ? prefs.travelStyle() : null,
            List.of()
        );

        var generated = aiProvider.suggestActivities(request);
        var toSave = generated.stream()
            .map(s -> Suggestion.from(tripId, date, s))
            .toList();

        suggestionRepository.saveAll(toSave);
        return toSave;
    }
}
