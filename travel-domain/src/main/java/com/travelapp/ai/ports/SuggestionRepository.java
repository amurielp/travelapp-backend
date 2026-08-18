package com.travelapp.ai.ports;

import com.travelapp.ai.domain.Suggestion;
import java.time.LocalDate;
import java.util.*;

public interface SuggestionRepository {
    List<Suggestion> findByTripIdAndDate(UUID tripId, LocalDate date);
    Optional<Suggestion> findById(UUID id);
    Suggestion save(Suggestion suggestion);
    void saveAll(List<Suggestion> suggestions);
}
