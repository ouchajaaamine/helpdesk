package com.example.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // ✅ ADD: Ignore Hibernate proxy properties
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)  // ✅ Remove nullable = false
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column  // ✅ Remove nullable = false  
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; 
        
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "activation_token")
    private String activationToken;

    @Column(name = "token_expiry")
    private LocalDateTime tokenExpiry;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    private Boolean isFirstLogin;


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