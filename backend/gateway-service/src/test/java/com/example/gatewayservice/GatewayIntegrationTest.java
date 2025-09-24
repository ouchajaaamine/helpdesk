package com.example.gatewayservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = {
        "eureka.client.enabled=false", // Disable Eureka for testing
        "spring.cloud.gateway.discovery.locator.enabled=false" // Use static routes only
})
class GatewayIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldRouteToUserService() {
        // This test will attempt to hit a user-service endpoint through the gateway.
        // Since authentication is required, we expect a 401 Unauthorized response.
        // The goal is to verify the gateway attempts to route (not a 404).
        webTestClient.get().uri("/api/users/1")
                .exchange()
                .expectStatus().isUnauthorized(); // Expecting 401 because authentication is required
    }
}