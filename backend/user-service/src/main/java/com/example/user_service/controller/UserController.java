package com.example.user_service.controller;

import com.example.user_service.entity.SignUpRequest;
import com.example.user_service.entity.User;
import com.example.user_service.services.UserService;

import java.util.List;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // POST endpoint for employee sign-up
    @PostMapping("/signup")
    public <User> ResponseEntity<User> signUp(@RequestBody SignUpRequest request) {
        try {
            User user = (User) userService.submitSignUpRequest(request.getFirstName(), request.getLastName(), request.getEmail());
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Return 400 Bad Request for validation errors
        }

    }
  
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}       