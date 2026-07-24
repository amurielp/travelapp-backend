package com.travelapp.ai.ports;
import com.travelapp.ai.domain.*;
import java.util.List;
public interface AIProvider {
    AIResponse complete(AIRequest request);
    ParsedDocument parseTravelDocument(String rawText);
    List<ActivitySuggestion> suggestActivities(SuggestionRequest request);
    String providerName();
}
