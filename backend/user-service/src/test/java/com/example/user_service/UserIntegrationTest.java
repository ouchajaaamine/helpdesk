package com.example.user_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class UserIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        // This test simply checks if the Spring application context loads successfully.
        // It's a good basic check to ensure all configurations are correct.
    }

    @Test
    void shouldSaveAndFindUser() {
        // What: Create a new User entity.
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123"); // In a real app, passwords should be hashed!
        user.setFirstName("Test");
        user.setLastName("User");

        // Why: Save the user to the database via the repository.
        // This tests the persistence mechanism.
        User savedUser = userRepository.save(user);

        // What: Verify the user was saved and has an ID.
        // Why: Ensure the database interaction was successful and the entity was persisted.
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();

        // What: Find the user by their ID.
        // Why: Test the retrieval mechanism from the database.
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        // What: Assert that the found user matches the saved user.
        // Why: Confirm data integrity and correct retrieval.
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testuser");
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
    }
}