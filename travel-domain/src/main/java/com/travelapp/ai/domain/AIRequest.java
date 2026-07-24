package com.travelapp.ai.domain;
public record AIRequest(String systemPrompt, String userPrompt, int maxTokens, double temperature, String responseFormat) {
    public static AIRequest forExtraction(String system, String user) { return new AIRequest(system, user, 1024, 0.1, "json"); }
    public static AIRequest forSuggestions(String system, String user) { return new AIRequest(system, user, 2048, 0.7, "json"); }
}
