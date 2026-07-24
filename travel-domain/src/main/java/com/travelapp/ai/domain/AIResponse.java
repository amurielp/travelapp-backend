package com.travelapp.ai.domain;
public record AIResponse(String content, TokenUsage usage, String providerName, boolean fromCache) {}
