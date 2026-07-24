package com.travelapp.web.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.*;
import java.util.stream.Stream;

/**
 * Extrae roles del claim realm_access.roles de Keycloak
 * y los convierte en GrantedAuthority con prefijo ROLE_
 */
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        var realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) return List.of();

        @SuppressWarnings("unchecked")
        var roles = (List<String>) realmAccess.getOrDefault("roles", List.of());

        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
            .map(a -> (GrantedAuthority) a)
            .toList();
    }
}
