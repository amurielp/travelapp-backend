package com.travelapp.ai.adapters.ollama;

public class OllamaUnavailableException extends RuntimeException {
    public OllamaUnavailableException(String msg, Throwable cause) { super(msg, cause); }
}
