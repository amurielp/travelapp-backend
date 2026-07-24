package com.travelapp.ai.adapters.ollama;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.ollama")
public class OllamaProperties {
    private String baseUrl = "http://localhost:11434";
    private String model   = "llama3.2";
}
