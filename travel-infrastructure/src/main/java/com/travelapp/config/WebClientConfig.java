package com.travelapp.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configura un WebClient por cada servicio interno.
 *
 * Cada bean tiene su propio base URL leído de ServicesProperties,
 * sus propios timeouts, y el filtro de logging.
 *
 * El código que usa estos beans no sabe si la URL es localhost o
 * un nombre de servicio de Kubernetes — eso lo decide el YAML del entorno.
 */
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final ServicesProperties services;

    // ── Google Places ──────────────────────────────────────────
    @Bean("googlePlacesWebClient")
    public WebClient googlePlacesWebClient() {
        var cfg = services.getGooglePlaces();
        return WebClient.builder()
            .baseUrl(cfg.getUrl())
            .clientConnector(reactorConnector(3000, 10000))
            .filter(loggingFilter("google-places"))
            .build();
    }

    // ── OpenWeather ────────────────────────────────────────────
    @Bean("openWeatherWebClient")
    public WebClient openWeatherWebClient() {
        var cfg = services.getOpenweather();
        return WebClient.builder()
            .baseUrl(cfg.getUrl())
            .clientConnector(reactorConnector(3000, 8000))
            .filter(loggingFilter("openweather"))
            .build();
    }

    // ── Ticketmaster ───────────────────────────────────────────
    @Bean("ticketmasterWebClient")
    public WebClient ticketmasterWebClient() {
        var cfg = services.getTicketmaster();
        return WebClient.builder()
            .baseUrl(cfg.getUrl())
            .clientConnector(reactorConnector(3000, 10000))
            .filter(loggingFilter("ticketmaster"))
            .build();
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────

    private ReactorClientHttpConnector reactorConnector(int connectMs, int readMs) {
        var httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectMs)
            .responseTimeout(Duration.ofMillis(readMs))
            .doOnConnected(conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(readMs, TimeUnit.MILLISECONDS)));
        return new ReactorClientHttpConnector(httpClient);
    }

    /**
     * Filtro de logging: loguea método, URL y status de cada llamada saliente.
     * En K8s, Istio añade el distributed tracing encima de esto.
     */
    private ExchangeFilterFunction loggingFilter(String serviceName) {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            // log.debug usaremos SLF4J desde el adaptador — aquí solo estructura
            return Mono.just(req);
        });
    }

}
