package com.example.ticketservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class KeycloakSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints (no authentication required)
                .requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()
                // Protected endpoints (authentication required)
                .requestMatchers("/api/tickets/**", "/api/files/**").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
        return converter;
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Extract roles from Keycloak JWT token
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        
        System.out.println("DEBUG: JWT Subject: " + jwt.getSubject());
        System.out.println("DEBUG: JWT Claims: " + jwt.getClaims());
        System.out.println("DEBUG: Realm Access: " + realmAccess);
        
        if (realmAccess != null && realmAccess.get("roles") != null) {
            List<String> roles = (List<String>) realmAccess.get("roles");
            System.out.println("DEBUG: All roles from Keycloak: " + roles);
            
            List<GrantedAuthority> authorities = roles.stream()
                    // Filter out Keycloak default roles, keep only our business roles
                    .filter(role -> !role.startsWith("default-roles-") && 
                                   !role.equals("offline_access") && 
                                   !role.equals("uma_authorization"))
                    // Add ROLE_ prefix for Spring Security and convert to uppercase
                    .map(role -> {
                        String upperCaseRole = role.toUpperCase();
                        System.out.println("DEBUG: Mapping role: " + role + " -> ROLE_" + upperCaseRole);
                        return new SimpleGrantedAuthority("ROLE_" + upperCaseRole);
                    })
                    .collect(Collectors.toList());
            
            System.out.println("DEBUG: Final authorities: " + authorities);
            return authorities;
        }
        
        System.out.println("DEBUG: No realm_access found, returning empty authorities");
        return List.of();
    }
}
