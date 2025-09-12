package com.example.ticketservice.repository;

import com.example.ticketservice.entity.TicketStatus;
import com.example.ticketservice.entity.Priority;
import com.example.ticketservice.entity.Ticket;
import com.example.ticketservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Find tickets by status
    List<Ticket> findByStatus(TicketStatus status);

    // Find tickets by priority
    List<Ticket> findByPriority(Priority priority);

    // Find tickets by category
    List<Ticket> findByCategory(Category category);

    // Find tickets created by a specific user
    List<Ticket> findByCreatedById(Long createdById);

    // Find tickets assigned to a specific technician
    List<Ticket> findByAssignedToId(Long assignedToId);

    // Find tickets by status and assigned user
    List<Ticket> findByStatusAndAssignedToId(TicketStatus status, Long assignedToId);

    // Find open tickets (not resolved or closed)
    @Query("SELECT t FROM Ticket t WHERE t.status NOT IN ('RESOLVED', 'CLOSED')")
    List<Ticket> findOpenTickets();

    // Find overdue tickets
    @Query("SELECT t FROM Ticket t WHERE t.slaDeadline < :currentTime AND t.status NOT IN ('RESOLVED', 'CLOSED')")
    List<Ticket> findOverdueTickets(@Param("currentTime") LocalDateTime currentTime);

    // Find tickets created within date range
    List<Ticket> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Count tickets by status
    long countByStatus(TicketStatus status);

    // Count tickets by priority
    long countByPriority(Priority priority);

    // Count tickets assigned to a technician
    long countByAssignedToId(Long assignedToId);

    // Find tickets by title or description containing text
    @Query("SELECT t FROM Ticket t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Ticket> searchTickets(@Param("searchText") String searchText);

    // Find unassigned tickets
    @Query("SELECT t FROM Ticket t WHERE t.assignedToId IS NULL AND t.status = 'OPEN'")
    List<Ticket> findUnassignedTickets();

    // Find tickets by multiple criteria
    List<Ticket> findByStatusAndPriorityAndCategory(
        TicketStatus status,
        Priority priority,
        Category category
    );
}
