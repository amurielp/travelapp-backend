package com.travelapp.ai.adapters.claude;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelapp.ai.adapters.shared.*;
import com.travelapp.ai.domain.*;
import com.travelapp.ai.ports.AIProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Adaptador Claude — activo cuando ai.provider=claude.
 * Llama directamente a la Anthropic API.
 * Usado en producción cuando no se quiere el ai-service Python.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "claude")
public class ClaudeAdapter implements AIProvider {

    private static final String MODEL       = "claude-sonnet-4-6";
    private static final double INPUT_COST  = 2.70;   // USD por millón de tokens
    private static final double OUTPUT_COST = 13.50;

    private final WebClient    client;
    private final ObjectMapper json;

    public ClaudeAdapter(
            @Qualifier("claudeWebClient") WebClient client,
            ObjectMapper json) {
        this.client = client;
        this.json   = json;
        log.info("AI provider: Claude @ {}", MODEL);
    }

    @Override
    public AIResponse complete(AIRequest req) {
        try {
            var body = Map.of(
                "model",      MODEL,
                "max_tokens", req.maxTokens(),
                "system",     req.systemPrompt(),
                "messages",   List.of(Map.of("role", "user", "content", req.userPrompt()))
            );

            var raw    = client.post().uri("")
                .bodyValue(json.writeValueAsString(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();

            var parsed = json.readTree(raw);
            int in     = parsed.at("/usage/input_tokens").asInt();
            int out    = parsed.at("/usage/output_tokens").asInt();
            double cost = (in / 1_000_000.0) * INPUT_COST + (out / 1_000_000.0) * OUTPUT_COST;
            String content = parsed.at("/content/0/text").asText();

            return new AIResponse(content, new TokenUsage(in, out, cost), providerName(), false);

        } catch (Exception e) {
            throw new RuntimeException("Claude API error: " + e.getMessage(), e);
        }
    }

    @Override
    public ParsedDocument parseTravelDocument(String rawText) {
        return ParsedDocumentMapper.fromJson(
            complete(AIRequest.forExtraction(
                PromptLibrary.PDF_PARSER_SYSTEM,
                PromptLibrary.pdfParserUser(rawText)
            )).content()
        );
    }

    @Override
    public List<ActivitySuggestion> suggestActivities(SuggestionRequest req) {
        return ActivitySuggestionMapper.fromJson(
            complete(AIRequest.forSuggestions(
                PromptLibrary.SUGGESTIONS_SYSTEM,
                PromptLibrary.suggestionsUser(req)
            )).content()
        );
    }

    @Override
    public String providerName() { return "claude/" + MODEL; }
}
