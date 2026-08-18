package com.travelapp.ai.usecases;

import com.travelapp.ai.domain.*;
import com.travelapp.ai.ports.SuggestionRepository;
import com.travelapp.events.domain.*;
import com.travelapp.events.usecases.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AcceptSuggestionUseCase {

    private final SuggestionRepository suggestionRepository;
    private final CreateEventUseCase   createEvent;

    @Transactional
    public TravelEvent execute(UUID suggestionId, UUID tripId) {
        var suggestion = suggestionRepository.findById(suggestionId)
            .orElseThrow(() -> new IllegalArgumentException("Suggestion not found: " + suggestionId));

        var startDatetime = OffsetDateTime.of(
            suggestion.getDate(), LocalTime.of(10, 0), ZoneOffset.UTC);
        var endDatetime = suggestion.getEstimatedDurationMin() > 0
            ? startDatetime.plusMinutes(suggestion.getEstimatedDurationMin())
            : startDatetime.plusHours(2);

        var command = new CreateEventCommand(
            tripId,
            null,
            EventType.ACTIVITY,
            suggestion.getName(),
            suggestion.getReasoning(),
            null,
            startDatetime,
            endDatetime,
            false,
            ZoneId.of("Europe/Madrid"),
            EventSource.AI_SUGGESTION,
            suggestion.getName(),
            suggestion.getLatitude(),
            suggestion.getLongitude(),
            null, null, null, null,
            null, null,
            null
        );

        var event = createEvent.execute(command);
        suggestion.accept();
        suggestionRepository.save(suggestion);
        return event;
    }
}
