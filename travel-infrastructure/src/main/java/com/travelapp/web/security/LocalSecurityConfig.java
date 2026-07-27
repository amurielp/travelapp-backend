package com.travelapp.web.security;

import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Slf4j
@Configuration
@EnableWebSecurity
@Profile("local")
public class LocalSecurityConfig {

    // UUID fijo del usuario de test local
    public static final String LOCAL_USER_ID    = "00000000-0000-0000-0000-000000000001";
    public static final String LOCAL_USER_EMAIL = "test@travelapp.local";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(cors -> cors.configurationSource(corsSource()))
            .addFilterBefore(mockJwtFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    /** Inyecta un JWT de test en cada petición para que @AuthenticationPrincipal Jwt no sea null. */
    private Filter mockJwtFilter() {
        return (ServletRequest req, ServletResponse res, FilterChain chain) -> {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                var jwt = Jwt.withTokenValue("mock-local-token")
                    .header("alg", "none")
                    .subject(LOCAL_USER_ID)
                    .claim("email", LOCAL_USER_EMAIL)
                    .claim("name",  "Test User")
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(86400))
                    .build();
                SecurityContextHolder.getContext()
                    .setAuthentication(new JwtAuthenticationToken(jwt, List.of()));
            }
            chain.doFilter(req, res);
        };
    }

    @Bean
    public ApplicationRunner localTestUserLoader(JdbcTemplate jdbc) {
        return args -> jdbc.update("""
            INSERT INTO users (id, keycloak_id, email, name, plan, preferences, created_at, updated_at)
            VALUES (?, 'local-test-user', ?, 'Test User', 'free', '{}', NOW(), NOW())
            ON CONFLICT (keycloak_id) DO UPDATE
              SET id         = EXCLUDED.id,
                  email      = EXCLUDED.email,
                  updated_at = NOW()
            """, UUID.fromString(LOCAL_USER_ID), LOCAL_USER_EMAIL);
    }

    @Bean
    public CorsConfigurationSource corsSource() {
        var config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
