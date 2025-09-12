package com.example.ticketservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    // In a real implementation, you'd inject a UserRepository or FeignClient to user-service
    // For now, we'll use hardcoded users that match our sample data

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // This would typically call your user-service
        // For demonstration, we'll return mock user details

        String role = getRoleForUser(username);
        String password = "$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4"; // Same as sample data

        return User.builder()
                .username(username)
                .password(password)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)))
                .build();
    }

    private String getRoleForUser(String username) {
        // Mock role assignment based on email patterns
        if (username.contains("admin")) {
            return "ADMIN";
        } else if (username.contains("tech") || username.contains("support")) {
            return "TECHNICIAN";
        } else {
            return "EMPLOYEE";
        }
    }
}
