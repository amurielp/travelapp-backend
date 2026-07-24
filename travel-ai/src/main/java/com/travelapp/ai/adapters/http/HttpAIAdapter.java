package com.travelapp.ai.adapters.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelapp.ai.domain.*;
import com.travelapp.ai.ports.AIProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "service")
public class HttpAIAdapter implements AIProvider {

    private final WebClient    client;
    private final ObjectMapper json;

    public HttpAIAdapter(
            @Qualifier("aiServiceWebClient") WebClient client,
            ObjectMapper json) {
        this.client = client;
        this.json   = json;
        log.info("AI provider: HTTP → ai-service");
    }

    @Override
    public AIResponse complete(AIRequest req) {
        try {
            var body = Map.of(
                "system_prompt", req.systemPrompt(),
                "user_prompt",   req.userPrompt(),
                "max_tokens",    req.maxTokens(),
                "temperature",   req.temperature()
            );
            var raw = client.post().uri("/complete")
                .bodyValue(json.writeValueAsString(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();

            var parsed = json.readTree(raw);
            return new AIResponse(
                parsed.at("/content").asText(),
                new TokenUsage(0, 0, 0.0),
                providerName(), false
            );
        } catch (Exception e) {
            throw new AIServiceUnavailableException("ai-service unreachable: " + e.getMessage(), e);
        }
    }

    @Override
    public ParsedDocument parseTravelDocument(String rawText) {
        try {
            var body = Map.of("raw_text", rawText);
            var raw  = client.post().uri("/parse")
                .bodyValue(json.writeValueAsString(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();
            return json.readValue(raw, ParsedDocument.class);
        } catch (Exception e) {
            throw new AIServiceUnavailableException("parse failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ActivitySuggestion> suggestActivities(SuggestionRequest req) {
        try {
            var raw = client.post().uri("/suggest")
                .bodyValue(json.writeValueAsString(req))
                .retrieve()
                .bodyToMono(String.class)
                .block();
            return json.readValue(raw, new TypeReference<>() {});
        } catch (Exception e) {
            throw new AIServiceUnavailableException("suggest failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String providerName() { return "http/ai-service"; }
}
