package com.example.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "User entity representing system users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "User's first name", example = "John", required = true)
    private String firstName;

    @Column(nullable = false)
    @Schema(description = "User's last name", example = "Doe", required = true)
    private String lastName;

    @Column(unique = true)  
    @Schema(description = "Unique username for login", example = "john.doe")
    private String username;

    @Column(nullable = false, unique = true)
    @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
    private String email;

    @Column  // ✅ Remove nullable = false  
    @Schema(description = "User's password", example = "password123", required = true)
    private String password;

    @Enumerated(EnumType.STRING)
    @Schema(description = "User's role in the system", example = "ADMIN")
    private Role role; 
        
    @Enumerated(EnumType.STRING)
    @Schema(description = "User's account status", example = "ACTIVE")
    private Status status;

    @Column(name = "created_at")
    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @Schema(description = "Account last update timestamp")
    private LocalDateTime updatedAt;

    @Column(name = "activation_token")
    @Schema(description = "Token for account activation")
    private String activationToken;

    @Column(name = "token_expiry")
    @Schema(description = "Token expiry timestamp")
    private LocalDateTime tokenExpiry;

    @Column(name = "approval_date")
    @Schema(description = "Date when the account was approved")
    private LocalDateTime approvalDate;

    @Schema(description = "Indicates if it's the user's first login")
    private Boolean isFirstLogin;

    // 🔐 KEYCLOAK INTEGRATION FIELD
    @Column(name = "keycloak_user_id")
    @Schema(description = "Keycloak user ID for linking accounts", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String keycloakUserId;

    // 🗓️ ADDITIONAL TRACKING FIELDS
    @Column(name = "rejection_reason")
    @Schema(description = "Reason for account rejection (if applicable)")
    private String rejectionReason;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if(status==null){
            status= Status.PENDING;
        }

         // Set default first login flag
    if (isFirstLogin == null) {
        isFirstLogin = true;
    }

    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isTokenExpired(){
        return tokenExpiry != null && LocalDateTime.now().isAfter(tokenExpiry);
    }

    public boolean isPending(){
        return Status.PENDING.equals(status);
    }

    public boolean isActive(){
        return Status.ACTIVE.equals(status);
    }


}