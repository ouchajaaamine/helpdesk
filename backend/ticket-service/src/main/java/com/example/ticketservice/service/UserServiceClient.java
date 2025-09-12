package com.example.ticketservice.service;

import com.example.ticketservice.entity.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceClient {

    private static final Logger log = LoggerFactory.getLogger(UserServiceClient.class);

    private final RestTemplate restTemplate;
    private final String USER_SERVICE_URL = "http://localhost:8081"; // User service port

    @Autowired
    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Get all available technicians from user service
     */
    public List<UserDTO> getTechnicians() {
        String url = USER_SERVICE_URL + "/api/users/technicians";

        try {
            log.debug("Fetching technicians from user service: {}", url);

            ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDTO>>() {}
            );

            List<UserDTO> technicians = response.getBody();
            log.debug("Retrieved {} technicians", technicians != null ? technicians.size() : 0);

            return technicians != null ? technicians : Collections.emptyList();

        } catch (Exception e) {
            log.error("Failed to fetch technicians from user service: {}", e.getMessage());
            // Return empty list instead of throwing exception for better UX
            return Collections.emptyList();
        }
    }

    /**
     * Get a specific user by ID from user service
     */
    public UserDTO getUserById(Long userId) {
        String url = USER_SERVICE_URL + "/api/users/" + userId;

        try {
            log.debug("Fetching user {} from user service", userId);

            ResponseEntity<UserDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                UserDTO.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Failed to fetch user {} from user service: {}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * Validate if a user is a valid technician
     */
    public boolean isValidTechnician(Long userId) {
        UserDTO user = getUserById(userId);

        if (user == null) {
            log.warn("User {} not found", userId);
            return false;
        }

        boolean isTechnician = "TECHNICIAN".equals(user.getRole());
        boolean isActive = "ACTIVE".equals(user.getStatus());

        log.debug("User {} validation: role={}, status={}, valid={}",
                 userId, user.getRole(), user.getStatus(), isTechnician && isActive);

        return isTechnician && isActive;
    }
}
