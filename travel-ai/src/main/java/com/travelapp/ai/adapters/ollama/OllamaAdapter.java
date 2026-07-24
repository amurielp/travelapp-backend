package com.travelapp.ai.adapters.ollama;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelapp.ai.adapters.shared.ActivitySuggestionMapper;
import com.travelapp.ai.adapters.shared.ParsedDocumentMapper;
import com.travelapp.ai.adapters.shared.PromptLibrary;
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
 * Adaptador Ollama — activo cuando ai.provider=ollama.
 * Usado en desarrollo local y en piloto (gratuito, sin API key).
 *
 * Requiere Ollama corriendo en localhost:11434 (o la URL configurada).
 * Modelo configurado via ai.ollama.model (default: llama3.2).
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "ollama")
public class OllamaAdapter implements AIProvider {

    private final WebClient        client;
    private final ObjectMapper     json;
    private final OllamaProperties props;

    public OllamaAdapter(
            @Qualifier("ollamaWebClient") WebClient client,
            ObjectMapper json,
            OllamaProperties props) {
        this.client = client;
        this.json   = json;
        this.props  = props;
        log.info("AI provider: Ollama @ {} model={}", props.getBaseUrl(), props.getModel());
    }

    @Override
    public AIResponse complete(AIRequest req) {
        try {
            var body = Map.of(
                "model",   props.getModel(),
                "stream",  false,
                "messages", List.of(
                    Map.of("role", "system", "content", req.systemPrompt()),
                    Map.of("role", "user",   "content", req.userPrompt())
                ),
                "options", Map.of(
                    "temperature", req.temperature(),
                    "num_predict", req.maxTokens()
                )
            );

            var raw = client.post()
                .uri("/api/chat")
                .bodyValue(json.writeValueAsString(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();

            var parsed     = json.readTree(raw);
            int inputTok   = parsed.at("/prompt_eval_count").asInt(0);
            int outputTok  = parsed.at("/eval_count").asInt(0);
            String content = parsed.at("/message/content").asText();

            return new AIResponse(content, new TokenUsage(inputTok, outputTok, 0.0), providerName(), false);

        } catch (Exception e) {
            throw new OllamaUnavailableException("Ollama error: " + e.getMessage(), e);
        }
    }

    @Override
    public ParsedDocument parseTravelDocument(String rawText) {
        var response = complete(AIRequest.forExtraction(
            PromptLibrary.PDF_PARSER_SYSTEM,
            PromptLibrary.pdfParserUser(rawText)
        ));
        return ParsedDocumentMapper.fromJson(response.content());
    }

    @Override
    public List<ActivitySuggestion> suggestActivities(SuggestionRequest req) {
        var response = complete(AIRequest.forSuggestions(
            PromptLibrary.SUGGESTIONS_SYSTEM,
            PromptLibrary.suggestionsUser(req)
        ));
        return ActivitySuggestionMapper.fromJson(response.content());
    }

    @Override
    public String providerName() { return "ollama/" + props.getModel(); }
}
