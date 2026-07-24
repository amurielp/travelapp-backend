package com.travelapp.ai.config;

import com.travelapp.ai.adapters.ollama.OllamaProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class AiModuleConfig {

    @Bean("ollamaWebClient")
    public WebClient ollamaWebClient(OllamaProperties props) {
        return WebClient.builder()
            .baseUrl(props.getBaseUrl())
            .clientConnector(reactorConnector(5_000, 120_000))   // Ollama puede tardar
            .build();
    }

    @Bean("claudeWebClient")
    public WebClient claudeWebClient(
            @Value("${ai.claude.api-key:}") String apiKey) {
        return WebClient.builder()
            .baseUrl("https://api.anthropic.com/v1/messages")
            .defaultHeader("x-api-key",           apiKey)
            .defaultHeader("anthropic-version",    "2023-06-01")
            .defaultHeader("Content-Type",         "application/json")
            .clientConnector(reactorConnector(5_000, 60_000))
            .build();
    }

    @Bean("aiServiceWebClient")
    public WebClient aiServiceWebClient(
            @Value("${services.ai-service.url:http://localhost:8000}") String url) {
        return WebClient.builder()
            .baseUrl(url)
            .clientConnector(reactorConnector(5_000, 90_000))
            .build();
    }

    private ReactorClientHttpConnector reactorConnector(int connectMs, int readMs) {
        var http = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectMs)
            .doOnConnected(conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(readMs, TimeUnit.MILLISECONDS)));
        return new ReactorClientHttpConnector(http);
    }
}
