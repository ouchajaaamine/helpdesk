package com.example.user_service.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Keycloak configuration for creating admin client
 * This allows the User Service to manage users in Keycloak programmatically
 */
@Configuration
public class KeycloakConfig {
    
    @Autowired
    private KeycloakProperties keycloakProperties;
    
    /**
     * Creates Keycloak admin client bean
     * This client can create, update, enable/disable users in Keycloak
     */
    @Bean
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getServerUrl())     // http://localhost:8180
                .realm("master")                                   // Admin realm for managing other realms
                .grantType(OAuth2Constants.PASSWORD)              // Username/password authentication
                .username(keycloakProperties.getAdmin().getUsername()) // admin
                .password(keycloakProperties.getAdmin().getPassword()) // admin123
                .clientId(keycloakProperties.getAdmin().getClientId()) // admin-cli
                .build();
    }
}
