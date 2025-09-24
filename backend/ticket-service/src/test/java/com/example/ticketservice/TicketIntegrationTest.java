package com.example.ticketservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.ticketservice.entity.Ticket;
import com.example.ticketservice.entity.Priority;
import com.example.ticketservice.entity.TicketStatus;
import com.example.ticketservice.entity.Category;
import com.example.ticketservice.repository.TicketRepository;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class TicketIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void contextLoads() {
        // This test simply checks if the Spring application context loads successfully.
        // It's a good basic check to ensure all configurations are correct.
    }

    @Test
    void shouldSaveAndFindTicket() {
        // What: Create a new Ticket entity with all required fields.
        Ticket ticket = new Ticket();
        ticket.setTitle("Test Ticket");
        ticket.setDescription("This is a test ticket description.");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(Priority.MEDIUM);
        ticket.setCategory(Category.SOFTWARE); // FIX: Add the missing required category field
        ticket.setCreatedById(1L); // FIX: Add the missing required createdById field

        // Why: Save the ticket to the database via the repository.
        // This tests the persistence mechanism.
        Ticket savedTicket = ticketRepository.save(ticket);

        // What: Verify the ticket was saved and has an ID.
        // Why: Ensure the database interaction was successful and the entity was persisted.
        assertThat(savedTicket).isNotNull();
        assertThat(savedTicket.getId()).isNotNull();

        // What: Find the ticket by its ID.
        // Why: Test the retrieval mechanism from the database.
        Ticket foundTicket = ticketRepository.findById(savedTicket.getId()).orElse(null);

        // What: Assert that the found ticket matches the saved ticket.
        // Why: Confirm data integrity and correct retrieval.
        assertThat(foundTicket).isNotNull();
        assertThat(foundTicket.getTitle()).isEqualTo("Test Ticket");
        assertThat(foundTicket.getDescription()).isEqualTo("This is a test ticket description.");
        assertThat(foundTicket.getCategory()).isEqualTo(Category.SOFTWARE);
        assertThat(foundTicket.getCreatedById()).isEqualTo(1L);
    }
}