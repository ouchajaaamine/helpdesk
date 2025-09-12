package com.example.ticketservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_audit")
public class TicketAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    // Keep the ticketId field for easy querying
    @Column(name = "ticket_id", nullable = false, insertable = false, updatable = false)
    private Long ticketId;

    @Column(name = "action", nullable = false)
    private String action; // CREATE, UPDATE, DELETE, ASSIGN, STATUS_CHANGE

    @Column(name = "field_name")
    private String fieldName; // Which field was changed (title, status, priority, etc.)

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue; // Previous value

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue; // New value

    @Column(name = "changed_by", nullable = false)
    private Long changedBy; // User ID who made the change

    @Column(name = "changed_by_name")
    private String changedByName; // User name for display

    @Column(name = "change_reason")
    private String changeReason; // Optional reason for the change

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    // Default constructor
    public TicketAudit() {}

    // Constructor for creating audit records with Ticket object
    public TicketAudit(Ticket ticket, String action, String fieldName,
                      String oldValue, String newValue, Long changedBy,
                      String changedByName, String changeReason) {
        this.ticket = ticket;
        this.ticketId = ticket.getId(); // Set the ID for easy querying
        this.action = action;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedBy = changedBy;
        this.changedByName = changedByName;
        this.changeReason = changeReason;
        this.changedAt = LocalDateTime.now();
    }

    // Keep the old constructor for backward compatibility
    public TicketAudit(Long ticketId, String action, String fieldName,
                      String oldValue, String newValue, Long changedBy,
                      String changedByName, String changeReason) {
        this.ticketId = ticketId;
        this.action = action;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedBy = changedBy;
        this.changedByName = changedByName;
        this.changeReason = changeReason;
        this.changedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public Long getChangedBy() { return changedBy; }
    public void setChangedBy(Long changedBy) { this.changedBy = changedBy; }

    public String getChangedByName() { return changedByName; }
    public void setChangedByName(String changedByName) { this.changedByName = changedByName; }

    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
}
