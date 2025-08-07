package com.example.user_service.controller;

import com.example.user_service.entity.SignUpRequest;
import com.example.user_service.entity.User;
import com.example.user_service.services.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

   @GetMapping("/pending-users")
    public ResponseEntity<List<User>> getPendingUsers() {
    try {
        List<User> pendingUsers = userService.getPendingUsers();
        return ResponseEntity.ok(pendingUsers);
    } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
    }
}

    @GetMapping("pending-users/{id}")
    public ResponseEntity<User> getPendingUserById(@PathVariable long id){
        try {
            return ResponseEntity.ok(userService.getPendingUserById(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();

        }
    }

    @PostMapping("pending-users/{id}/approve")
    public ResponseEntity<User> approveUser(@PathVariable long id){
         try {
            User approvedUser = userService.approveUser(id);
            return ResponseEntity.ok(approvedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("users")
    public ResponseEntity<List<User>> getAllUsers(){
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
        return ResponseEntity.internalServerError().build();
        }
    }
            // ✅ ADD: Get only active users
        @GetMapping("/users/active")
        public ResponseEntity<List<User>> getActiveUsers() {
            try {
                List<User> activeUsers = userService.getActiveUsers();
                return ResponseEntity.ok(activeUsers);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        }

        // ✅ ADD: Get specific user (any status)
        @GetMapping("/users/{id}")
        public ResponseEntity<User> getUserById(@PathVariable long id) {
            try {
                User user = userService.getUserById(id);
                return ResponseEntity.ok(user);
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        }

}