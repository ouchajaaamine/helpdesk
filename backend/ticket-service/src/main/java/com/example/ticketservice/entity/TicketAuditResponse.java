package com.example.ticketservice.entity;

import java.time.LocalDateTime;

public class TicketAuditResponse {

    private Long id;
    private Long ticketId;
    private String action;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private Long changedBy;
    private String changedByName;
    private String changeReason;
    private LocalDateTime changedAt;

    // Default constructor
    public TicketAuditResponse() {}

    // Constructor from TicketAudit entity
    public TicketAuditResponse(TicketAudit audit) {
        this.id = audit.getId();
        this.ticketId = audit.getTicketId();
        this.action = audit.getAction();
        this.fieldName = audit.getFieldName();
        this.oldValue = audit.getOldValue();
        this.newValue = audit.getNewValue();
        this.changedBy = audit.getChangedBy();
        this.changedByName = audit.getChangedByName();
        this.changeReason = audit.getChangeReason();
        this.changedAt = audit.getChangedAt();
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
}
