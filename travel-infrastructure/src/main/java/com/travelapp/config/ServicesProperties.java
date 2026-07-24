package com.travelapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * Centraliza todas las URLs de servicios internos y externos.
 *
 * El código Java nunca hardcodea URLs — siempre inyecta ServicesProperties.
 * El entorno (local / pilot / k8s) sobreescribe los valores en el YAML.
 *
 * Local:  services.ai-service.url = http://localhost:8000
 * Pilot:  services.ai-service.url = http://ai-service:8000  (docker-compose)
 * K8s:    services.ai-service.url = http://ai-service        (Istio resuelve + mTLS)
 */
@Data
@Component
@ConfigurationProperties(prefix = "services")
public class ServicesProperties {

    private ServiceConfig aiService       = new ServiceConfig();
    private ServiceConfig travelCore      = new ServiceConfig();
    private ExternalApi   googlePlaces    = new ExternalApi();
    private ExternalApi   openweather     = new ExternalApi();
    private ExternalApi   ticketmaster    = new ExternalApi();
    private ExternalApi   openExchangeRates = new ExternalApi();

    @Data
    public static class ServiceConfig {
        private String url                = "http://localhost:8000";
        private int    connectTimeoutMs   = 3000;
        private int    readTimeoutMs      = 30000;
        private Retry  retry              = new Retry();

        @Data
        public static class Retry {
            private int maxAttempts = 2;
            private int backoffMs   = 500;
        }
    }

    @Data
    public static class ExternalApi {
        private String url;
        private String apiKey;
        private String appId;
    }
}
