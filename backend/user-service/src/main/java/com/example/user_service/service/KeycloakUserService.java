package com.example.user_service.service;

import com.example.user_service.config.KeycloakProperties;
import com.example.user_service.entity.Role;
import com.example.user_service.entity.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;

/**
 * Service for managing users in Keycloak
 * This service acts as a bridge between our User Service and Keycloak
 */
@Service
public class KeycloakUserService {
    
    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserService.class);
    
    @Autowired
    private Keycloak keycloakAdminClient;
    
    @Autowired
    private KeycloakProperties keycloakProperties;
    
    /**
     * Step 1: Create a new user in Keycloak (DISABLED by default)
     * This happens when a user signs up - they can't login until admin approves
     * 
     * @param user The user from our database
     * @param temporaryPassword A temporary password for the Keycloak account
     * @return The Keycloak user ID if successful, null if failed
     */
    public String createKeycloakUser(User user, String temporaryPassword) {
        try {
            logger.info("🔄 Creating Keycloak user for: {}", user.getEmail());
            
            // Get access to our realm (helpdesk-system)
            RealmResource realmResource = keycloakAdminClient.realm(keycloakProperties.getRealm());
            UsersResource usersResource = realmResource.users();
            
            // Create the user representation (like a blueprint)
            UserRepresentation userRep = new UserRepresentation();
            userRep.setUsername(user.getUsername());          // Username: john.doe
            userRep.setEmail(user.getEmail());                // Email: john.doe@techcorp.com
            userRep.setFirstName(user.getFirstName());        // First name: John
            userRep.setLastName(user.getLastName());          // Last name: Doe
            userRep.setEnabled(false);                        // 🔒 DISABLED - can't login yet!
            userRep.setEmailVerified(true);                   // We trust our signup process
            
            // Create the user in Keycloak
            Response response = usersResource.create(userRep);
            
            if (response.getStatus() == 201) { // 201 = Created successfully
                // Extract the user ID from the response
                String userId = extractUserIdFromResponse(response);
                logger.info("✅ Keycloak user created successfully with ID: {}", userId);
                
                // Set the temporary password
                setUserPassword(userId, temporaryPassword);
                
                // Assign the appropriate role (EMPLOYEE or TECHNICIAN)
                assignRoleToUser(userId, user.getRole());
                
                return userId;
                
            } else {
                logger.error("❌ Failed to create Keycloak user. Status: {}", response.getStatus());
                return null;
            }
            
        } catch (Exception e) {
            logger.error("💥 Error creating Keycloak user: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Step 2: Enable a Keycloak user (happens when admin approves)
     * This allows the user to finally login!
     * 
     * @param keycloakUserId The ID of the user in Keycloak
     * @return true if successful, false if failed
     */
    public boolean enableKeycloakUser(String keycloakUserId) {
        try {
            logger.info("🔓 Enabling Keycloak user: {}", keycloakUserId);
            
            // Get access to the specific user
            RealmResource realmResource = keycloakAdminClient.realm(keycloakProperties.getRealm());
            UserResource userResource = realmResource.users().get(keycloakUserId);
            
            // Get current user info and enable them
            UserRepresentation userRep = userResource.toRepresentation();
            userRep.setEnabled(true);  // 🔓 NOW they can login!
            
            // Update the user in Keycloak
            userResource.update(userRep);
            
            logger.info("✅ Keycloak user enabled successfully");
            return true;
            
        } catch (Exception e) {
            logger.error("💥 Error enabling Keycloak user: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Step 3: Disable a Keycloak user (happens when admin rejects or deactivates)
     * This prevents the user from logging in
     * 
     * @param keycloakUserId The ID of the user in Keycloak
     * @return true if successful, false if failed
     */
    public boolean disableKeycloakUser(String keycloakUserId) {
        try {
            logger.info("🔒 Disabling Keycloak user: {}", keycloakUserId);
            
            // Get access to the specific user
            RealmResource realmResource = keycloakAdminClient.realm(keycloakProperties.getRealm());
            UserResource userResource = realmResource.users().get(keycloakUserId);
            
            // Get current user info and disable them
            UserRepresentation userRep = userResource.toRepresentation();
            userRep.setEnabled(false);  // 🔒 Block login access
            
            // Update the user in Keycloak
            userResource.update(userRep);
            
            logger.info("✅ Keycloak user disabled successfully");
            return true;
            
        } catch (Exception e) {
            logger.error("💥 Error disabling Keycloak user: {}", e.getMessage(), e);
            return false;
        }
    }
    
    // ========================================
    // HELPER METHODS (The boring but necessary stuff)
    // ========================================
    
    /**
     * Extract user ID from Keycloak creation response
     * Keycloak returns the ID in the Location header
     */
    private String extractUserIdFromResponse(Response response) {
        String locationHeader = response.getHeaderString("Location");
        if (locationHeader != null) {
            return locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
        }
        return null;
    }
    
    /**
     * Set a temporary password for the user
     * Users will need to change this on first login
     */
    private void setUserPassword(String userId, String password) {
        try {
            RealmResource realmResource = keycloakAdminClient.realm(keycloakProperties.getRealm());
            UserResource userResource = realmResource.users().get(userId);
            
            // Create password credential
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(true);  // User must change on first login
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(password);
            
            userResource.resetPassword(passwordCred);
            logger.info("✅ Temporary password set for user");
            
        } catch (Exception e) {
            logger.error("💥 Error setting user password: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Assign role to user in Keycloak
     * Maps our User Service roles to Keycloak roles
     */
    private void assignRoleToUser(String userId, Role role) {
        try {
            RealmResource realmResource = keycloakAdminClient.realm(keycloakProperties.getRealm());
            UserResource userResource = realmResource.users().get(userId);
            
            // Map our roles to Keycloak role names
            String keycloakRoleName = mapRoleToKeycloak(role);
            
            // Get the role representation from Keycloak
            RoleRepresentation roleRep = realmResource.roles().get(keycloakRoleName).toRepresentation();
            
            // Assign the role to the user
            userResource.roles().realmLevel().add(Arrays.asList(roleRep));
            
            logger.info("✅ Role {} assigned to user", keycloakRoleName);
            
        } catch (Exception e) {
            logger.error("💥 Error assigning role to user: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Map our User Service roles to Keycloak role names
     */
    private String mapRoleToKeycloak(Role role) {
        switch (role) {
            case ADMIN:
                return "ADMIN";
            case TECHNICIAN:
                return "TECHNICIAN";
            case EMPLOYEE:
                return "EMPLOYEE";
            default:
                return "EMPLOYEE"; // Default fallback
        }
    }
}
