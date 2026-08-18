package com.travelapp.ai.usecases;

import com.travelapp.ai.ports.SuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DismissSuggestionUseCase {

    private final SuggestionRepository suggestions;

    @Transactional
    public void execute(UUID suggestionId) {
        var suggestion = suggestions.findById(suggestionId)
            .orElseThrow(() -> new IllegalArgumentException("Suggestion not found: " + suggestionId));
        suggestion.dismiss();
        suggestions.save(suggestion);
    }
}
