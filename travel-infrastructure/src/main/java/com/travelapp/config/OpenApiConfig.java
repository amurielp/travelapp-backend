package com.travelapp.config;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
    private String issuerUri;

    @Bean
    public OpenAPI openAPI() {
        var secScheme = new SecurityScheme()
            .type(SecurityScheme.Type.OAUTH2)
            .flows(new OAuthFlows().authorizationCode(new OAuthFlow()
                .authorizationUrl(issuerUri + "/protocol/openid-connect/auth")
                .tokenUrl(issuerUri + "/protocol/openid-connect/token")
                .scopes(new Scopes().addString("openid", "OpenID").addString("profile", "Profile"))));
        return new OpenAPI()
            .info(new io.swagger.v3.oas.models.info.Info().title("TravelApp API").version("1.0.0"))
            .addSecurityItem(new SecurityRequirement().addList("keycloak"))
            .schemaRequirement("keycloak", secScheme);
    }
}
