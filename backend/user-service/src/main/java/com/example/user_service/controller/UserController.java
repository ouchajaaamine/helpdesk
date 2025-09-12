package com.example.user_service.controller;

import com.example.user_service.entity.Role;
import com.example.user_service.entity.SignUpRequest;
import com.example.user_service.entity.User;
import com.example.user_service.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "User Management", description = "APIs for user registration and profile management")
public class UserController {

    @Autowired
    private UserService userService;

    // POST endpoint for employee/technician sign-up
    @Operation(summary = "Sign up a new user", description = "Registers a new user (EMPLOYEE or TECHNICIAN) with the provided details. Account will be in PENDING status until admin approval.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or role"),
        @ApiResponse(responseCode = "409", description = "Conflict - email already exists")
    })
    @PostMapping("/signup")
    public <User> ResponseEntity<User> signUp(@Parameter(description = "User registration details") @RequestBody SignUpRequest request) {
        try {
            User user = (User) userService.submitSignUpRequest(request.getFirstName(), request.getLastName(), request.getEmail(), request.getRole());
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Return 400 Bad Request for validation errors
        }

    }
  
    @Operation(summary = "Get user by ID", description = "Retrieves user details by their unique ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN') or hasRole('EMPLOYEE')")
    public ResponseEntity<User> getUserById(@Parameter(description = "User ID") @PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ ADD: Update user profile
    @Operation(summary = "Update user profile", description = "Updates the profile information of an existing user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateProfile(@Parameter(description = "User ID") @PathVariable Long id, @Parameter(description = "Profile update data") @RequestBody UpdateProfileRequest request) {
        try {
            User updatedUser = userService.updateProfile(id, request.getFirstName(), request.getLastName(), request.getEmail());
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ ADD: Change password
    @Operation(summary = "Change user password", description = "Changes the password of an existing user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> changePassword(@Parameter(description = "User ID") @PathVariable Long id, @Parameter(description = "New password data") @RequestBody ChangePasswordRequest request) {
        try {
            User updatedUser = userService.changePassword(id, request.getNewPassword());
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}

// ✅ ADD: DTO classes for requests
@Schema(description = "Profile update request")
class UpdateProfileRequest {
    @Schema(description = "User's first name", example = "John", required = true)
    private String firstName;

    @Schema(description = "User's last name", example = "Doe", required = true)
    private String lastName;

    @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
    private String email;

    // Getters and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

@Schema(description = "Password change request")
class ChangePasswordRequest {
    @Schema(description = "New password", example = "newSecurePassword123", required = true)
    private String newPassword;

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}