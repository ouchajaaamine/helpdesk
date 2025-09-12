package com.example.ticketservice.service;

import com.example.ticketservice.entity.Ticket;
import com.example.ticketservice.entity.TicketAudit;
import com.example.ticketservice.repository.TicketAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {

    @Autowired
    private TicketAuditRepository auditRepository;

    /**
     * Log the creation of a new ticket
     */
    @Transactional
    public void logTicketCreation(Ticket ticket) {
        Long userId = getCurrentUserId();
        String userName = getCurrentUserName();

        TicketAudit audit = new TicketAudit(
            ticket,
            "CREATE",
            null, // No specific field for creation
            null, // No old value for creation
            "Ticket created with title: " + ticket.getTitle(),
            userId,
            userName,
            "Initial ticket creation"
        );

        auditRepository.save(audit);
    }

    /**
     * Log updates to a ticket by comparing old and new values
     */
    @Transactional
    public void logTicketUpdate(Ticket oldTicket, Ticket newTicket) {
        Long userId = getCurrentUserId();
        String userName = getCurrentUserName();

        // Compare key fields and log changes
        compareAndLogFieldChange(oldTicket, newTicket, "title", userId, userName);
        compareAndLogFieldChange(oldTicket, newTicket, "description", userId, userName);
        compareAndLogFieldChange(oldTicket, newTicket, "status", userId, userName);
        compareAndLogFieldChange(oldTicket, newTicket, "priority", userId, userName);
        compareAndLogFieldChange(oldTicket, newTicket, "category", userId, userName);
        compareAndLogFieldChange(oldTicket, newTicket, "assignedToId", userId, userName);
        compareAndLogFieldChange(oldTicket, newTicket, "resolution", userId, userName);
    }

    /**
     * Log ticket assignment to a technician
     */
    @Transactional
    public void logTicketAssignment(Ticket ticket, Long technicianId, String technicianName) {
        Long userId = getCurrentUserId();
        String userName = getCurrentUserName();

        TicketAudit audit = new TicketAudit(
            ticket,
            "ASSIGN",
            "assignedToId",
            ticket.getAssignedToId() != null ? ticket.getAssignedToId().toString() : "Unassigned",
            technicianId != null ? technicianId + " (" + technicianName + ")" : "Unassigned",
            userId,
            userName,
            "Ticket assigned to technician"
        );

        auditRepository.save(audit);
    }

    /**
     * Log ticket deletion
     */
    @Transactional
    public void logTicketDeletion(Ticket ticket) {
        Long userId = getCurrentUserId();
        String userName = getCurrentUserName();

        TicketAudit audit = new TicketAudit(
            ticket,
            "DELETE",
            null,
            "Ticket existed",
            "Ticket deleted",
            userId,
            userName,
            "Ticket permanently removed"
        );

        auditRepository.save(audit);
    }

    /**
     * Get audit trail for a specific ticket
     */
    public List<TicketAudit> getAuditTrailForTicket(Long ticketId) {
        return auditRepository.findByTicketIdOrderByChangedAtDesc(ticketId);
    }

    /**
     * Get recent audit records across all tickets
     */
    public List<TicketAudit> getRecentAuditRecords() {
        return auditRepository.findTop50ByOrderByChangedAtDesc();
    }

    /**
     * Helper method to compare field values and log changes
     */
    private void compareAndLogFieldChange(Ticket oldTicket, Ticket newTicket, String fieldName,
                                        Long userId, String userName) {
        try {
            Field field = Ticket.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            Object oldValue = field.get(oldTicket);
            Object newValue = field.get(newTicket);

            // Only log if values are different
            if (!java.util.Objects.equals(oldValue, newValue)) {
                TicketAudit audit = new TicketAudit(
                    newTicket,
                    "UPDATE",
                    fieldName,
                    oldValue != null ? oldValue.toString() : "null",
                    newValue != null ? newValue.toString() : "null",
                    userId,
                    userName,
                    "Field updated: " + fieldName
                );

                auditRepository.save(audit);
            }
        } catch (Exception e) {
            // Log the error but don't fail the operation
            System.err.println("Error logging field change for " + fieldName + ": " + e.getMessage());
        }
    }

    /**
     * Get current authenticated user ID
     */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String) {
            // In a real implementation, you'd extract user ID from JWT claims
            // For now, return a default value
            return 1L; // TODO: Extract from JWT token
        }
        return 0L; // System user
    }

    /**
     * Get current authenticated user name
     */
    private String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getName();
        }
        return "System";
    }
}
