package com.example.user_service.controller;

import com.example.user_service.entity.Role;
import com.example.user_service.entity.SignUpRequest;
import com.example.user_service.entity.Status;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserStats;
import com.example.user_service.services.UserService;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Admin User Management", description = "APIs for administrators to manage users")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private UserService userService;

   @GetMapping("/pending-users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all pending users", description = "Retrieve list of users awaiting admin approval")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved pending users"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<User>> getPendingUsers() {
    try {
        List<User> pendingUsers = userService.getPendingUsers();
        return ResponseEntity.ok(pendingUsers);
    } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
    }
}

    @GetMapping("pending-users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending user by ID", description = "Retrieve a specific pending user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved pending user"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<User> getPendingUserById(@Parameter(description = "User ID") @PathVariable long id){
        try {
            return ResponseEntity.ok(userService.getPendingUserById(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();

        }
    }

    @PostMapping("pending-users/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve pending user", description = "Approve a pending user registration and activate their account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully approved"),
        @ApiResponse(responseCode = "400", description = "Bad request - user not pending"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> approveUser(@Parameter(description = "User ID to approve") @PathVariable long id){
         try {
            User approvedUser = userService.approveUser(id);
            return ResponseEntity.ok(approvedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve complete list of all users in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all users"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<User>> getAllUsers(){
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
        return ResponseEntity.internalServerError().build();
        }
    }
            
        @GetMapping("/users/active")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Get active users", description = "Retrieve list of all active users")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved active users"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<List<User>> getActiveUsers() {
            try {
                List<User> activeUsers = userService.getActiveUsers();
                return ResponseEntity.ok(activeUsers);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        }

        @GetMapping("/users/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<User> getUserById(@Parameter(description = "User ID") @PathVariable long id) {
            try {
                User user = userService.getUserById(id);
                return ResponseEntity.ok(user);
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        }

        @PostMapping("/users/{id}/reject")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Reject pending user", description = "Reject a pending user registration with reason")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully rejected"),
            @ApiResponse(responseCode = "400", description = "Bad request - user not pending"),
            @ApiResponse(responseCode = "404", description = "User not found")
        })
        public ResponseEntity<User> rejectUser(@Parameter(description = "User ID") @PathVariable Long id, @Parameter(description = "Rejection reason") @RequestParam String reason) {
            try {
                User rejectedUser = userService.rejectUser(id, reason);
                return ResponseEntity.ok(rejectedUser);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }

        @PutMapping("/users/{id}/role")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Change user role", description = "Update a user's role (ADMIN, TECHNICIAN, EMPLOYEE)")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role successfully updated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid role")
        })
        public ResponseEntity<User> changeUserRole(@Parameter(description = "User ID") @PathVariable Long id, @Parameter(description = "New role") @RequestParam Role role) {
            try {
                User updatedUser = userService.changeUserRole(id, role);
                return ResponseEntity.ok(updatedUser);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }

        @PutMapping("/users/{id}/deactivate")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Deactivate user", description = "Deactivate a user account")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully deactivated"),
            @ApiResponse(responseCode = "404", description = "User not found")
        })
        public ResponseEntity<User> deactivateUser(@Parameter(description = "User ID") @PathVariable Long id) {
            try {
                User deactivatedUser = userService.deactivateUser(id);
                return ResponseEntity.ok(deactivatedUser);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }

        @GetMapping("/stats")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Get user statistics", description = "Retrieve user statistics for dashboard")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user statistics"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<UserStats> getUserStats() {
            try {
                UserStats stats = userService.getUserStats();
                return ResponseEntity.ok(stats);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        }

        @GetMapping("/users/search")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Search users", description = "Search users by name, email, or username")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved search results"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<List<User>> searchUsers(@Parameter(description = "Search query") @RequestParam String query) {
            try {
                List<User> users = userService.searchUsers(query);
                return ResponseEntity.ok(users);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        }

        @GetMapping("/users/role/{role}")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Get users by role", description = "Retrieve all users with a specific role")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users by role"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<List<User>> getUsersByRole(@Parameter(description = "User role") @PathVariable Role role) {
            try {
                List<User> users = userService.getUsersByRole(role);
                return ResponseEntity.ok(users);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        }

}