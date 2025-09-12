package com.example.user_service.services;

import com.example.user_service.entity.Role;
import com.example.user_service.entity.Status;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserStats;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.KeycloakUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 🔐 KEYCLOAK INTEGRATION
    @Autowired
    private KeycloakUserService keycloakUserService;
    @Override
    public User submitSignUpRequest(String firstName, String lastName, String email, Role role) {
        if (firstName == null || lastName == null || email == null || role == null) {
            throw new IllegalArgumentException("First name, last name, email, and role cannot be null");
        }
        
        // Validate that only EMPLOYEE and TECHNICIAN roles can sign up
        if (role != Role.EMPLOYEE && role != Role.TECHNICIAN) {
            throw new IllegalArgumentException("Only EMPLOYEE and TECHNICIAN roles are allowed for signup");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 🎯 STEP 1: Create user in User Service database
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setStatus(Status.PENDING);          // 🟡 PENDING - waiting for admin approval
        user.setRole(role);
        
        // Generate username from email (john.doe@techcorp.com → john.doe)
        String username = email.split("@")[0];
        user.setUsername(username);

        // Save to database first
        User savedUser = userRepository.save(user);
        
        // 🎯 STEP 2: Create user in Keycloak (DISABLED)
        try {
            // Generate temporary password for Keycloak account
            String temporaryPassword = "TempPass@" + System.currentTimeMillis();
            
            // Create disabled Keycloak account
            String keycloakUserId = keycloakUserService.createKeycloakUser(savedUser, temporaryPassword);
            
            if (keycloakUserId != null) {
                // 🔗 Link the accounts by storing Keycloak ID
                savedUser.setKeycloakUserId(keycloakUserId);
                savedUser = userRepository.save(savedUser);
                
                System.out.println("✅ User created successfully:");
                System.out.println("   📧 Email: " + savedUser.getEmail());
                System.out.println("   🆔 User Service ID: " + savedUser.getId());
                System.out.println("   🔑 Keycloak ID: " + keycloakUserId);
                System.out.println("   🔒 Status: PENDING (Keycloak account DISABLED)");
                
            } else {
                System.err.println("⚠️ Keycloak account creation failed, but User Service account created");
            }
            
        } catch (Exception e) {
            System.err.println("💥 Error creating Keycloak account: " + e.getMessage());
            // Continue anyway - User Service account is created
        }
        
        return savedUser;
    }
    @Override
    public List<User> getPendingUsers() {
        return userRepository.findByStatus(Status.PENDING);
    }
    @Override
    public User getUserById(long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    @Override
    public User getPendingUserById(long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (user.getStatus() != Status.PENDING) {
            throw new IllegalArgumentException("User with id: " + id + " is not in PENDING status");
        }
        
        return user;
    }

    
@Override
public List<User> getActiveUsers() {
    return userRepository.findByStatus(Status.ACTIVE);
}

    @Override
    public User approveUser(long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        if (!user.getStatus().equals(Status.PENDING)) {
            throw new IllegalStateException("User is not pending approval");
        }

        // 🎯 STEP 1: Update User Service account
        user.setUsername(generateUsername(user));
        user.setPassword(generateTemporaryPassword());
        user.setStatus(Status.ACTIVE);
        user.setApprovalDate(LocalDateTime.now());

        User approvedUser = userRepository.save(user);

        // 🎯 STEP 2: Enable Keycloak account
        try {
            if (approvedUser.getKeycloakUserId() != null) {
                keycloakUserService.enableKeycloakUser(approvedUser.getKeycloakUserId());
                
                System.out.println("✅ User approved successfully:");
                System.out.println("   📧 Email: " + approvedUser.getEmail());
                System.out.println("   🆔 User Service: ACTIVE");
                System.out.println("   🔑 Keycloak: ENABLED");
                System.out.println("   🚀 User can now login!");
                
            } else {
                System.err.println("⚠️ User approved but no Keycloak ID found - manual Keycloak setup required");
            }
        } catch (Exception e) {
            System.err.println("💥 Error enabling Keycloak account: " + e.getMessage());
            System.err.println("⚠️ User Service account approved but Keycloak account may need manual enabling");
        }

        return approvedUser;
    }


    // Helper methods
    private String generateUsername(User user) {
    return (user.getFirstName() + "." + user.getLastName()).toLowerCase();
}

private String generateTemporaryPassword() {
    String rawPassword = "temp" + System.currentTimeMillis();
    return passwordEncoder.encode(rawPassword);
}
@Override
public List<User> getAllUsers() {
return userRepository.findAll();
}

@Override
public User updateProfile(Long userId, String firstName, String lastName, String email) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setEmail(email);

    return userRepository.save(user);
}

@Override
public User changePassword(Long userId, String newPassword) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    user.setPassword(passwordEncoder.encode(newPassword));
    return userRepository.save(user);
}

@Override
public User rejectUser(Long userId, String reason) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (!user.getStatus().equals(Status.PENDING)) {
        throw new IllegalStateException("Can only reject pending users");
    }

    // 🎯 STEP 1: Update User Service account
    user.setStatus(Status.REJECTED);
    user.setRejectionReason(reason);
    
    User rejectedUser = userRepository.save(user);

    // 🎯 STEP 2: Keep Keycloak account disabled (it's already disabled from signup)
    try {
        if (rejectedUser.getKeycloakUserId() != null) {
            // Optionally disable to ensure it stays disabled
            keycloakUserService.disableKeycloakUser(rejectedUser.getKeycloakUserId());
            
            System.out.println("✅ User rejected:");
            System.out.println("   📧 Email: " + rejectedUser.getEmail());
            System.out.println("   ❌ Reason: " + reason);
            System.out.println("   🔒 Keycloak account remains disabled");
            
        } else {
            System.out.println("✅ User rejected (no Keycloak account found)");
        }
    } catch (Exception e) {
        System.err.println("⚠️ Error managing Keycloak account during rejection: " + e.getMessage());
        // Continue anyway - rejection is complete
    }

    return rejectedUser;
}

@Override
public User changeUserRole(Long userId, Role newRole) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    user.setRole(newRole);
    return userRepository.save(user);
}

@Override
public User deactivateUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    user.setStatus(Status.INACTIVE);
    return userRepository.save(user);
}

@Override
public UserStats getUserStats() {
    List<User> allUsers = userRepository.findAll();

    return new UserStats(
        allUsers.size(),
        (int) allUsers.stream().filter(u -> u.getStatus() == Status.PENDING).count(),
        (int) allUsers.stream().filter(u -> u.getStatus() == Status.ACTIVE).count(),
        (int) allUsers.stream().filter(u -> u.getRole() == Role.ADMIN).count(),
        (int) allUsers.stream().filter(u -> u.getRole() == Role.TECHNICIAN).count(),
        (int) allUsers.stream().filter(u -> u.getRole() == Role.EMPLOYEE).count()
    );
}

@Override
public List<User> searchUsers(String query) {
    return userRepository.findAll().stream()
        .filter(user ->
            user.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
            user.getLastName().toLowerCase().contains(query.toLowerCase()) ||
            user.getEmail().toLowerCase().contains(query.toLowerCase()) ||
            (user.getUsername() != null && user.getUsername().toLowerCase().contains(query.toLowerCase()))
        )
        .collect(Collectors.toList());
}

@Override
public List<User> getUsersByRole(Role role) {
    return userRepository.findByRole(role.name());
}


}
