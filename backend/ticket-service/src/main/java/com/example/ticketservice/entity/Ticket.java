package com.example.ticketservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(name = "created_by_id", nullable = false)
    private Long createdById;

    @Column(name = "assigned_to_id")
    private Long assignedToId;

    // Timestamp fields
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    // SLA tracking
    @Column(name = "sla_deadline")
    private LocalDateTime slaDeadline;

    @Column(name = "actual_resolution_time")
    private Long actualResolutionTime; // in minutes

    // Additional fields
    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Column(name = "actual_hours")
    private Integer actualHours;

    @Column(length = 500)
    private String resolution;

    @Column(name = "is_overdue")
    private Boolean isOverdue = false;

    // Default constructor
    public Ticket() {}

    // All args constructor
    public Ticket(String title, String description, TicketStatus status, Priority priority, Category category,
                  Long createdById, Long assignedToId, LocalDateTime createdAt, LocalDateTime updatedAt,
                  LocalDateTime resolvedAt, LocalDateTime closedAt, LocalDateTime slaDeadline,
                  Long actualResolutionTime, Integer estimatedHours, Integer actualHours, String resolution, Boolean isOverdue) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.createdById = createdById;
        this.assignedToId = assignedToId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.resolvedAt = resolvedAt;
        this.closedAt = closedAt;
        this.slaDeadline = slaDeadline;
        this.actualResolutionTime = actualResolutionTime;
        this.estimatedHours = estimatedHours;
        this.actualHours = actualHours;
        this.resolution = resolution;
        this.isOverdue = isOverdue;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }

    public Long getAssignedToId() { return assignedToId; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    public LocalDateTime getSlaDeadline() { return slaDeadline; }
    public void setSlaDeadline(LocalDateTime slaDeadline) { this.slaDeadline = slaDeadline; }

    public Long getActualResolutionTime() { return actualResolutionTime; }
    public void setActualResolutionTime(Long actualResolutionTime) { this.actualResolutionTime = actualResolutionTime; }

    public Integer getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; }

    public Integer getActualHours() { return actualHours; }
    public void setActualHours(Integer actualHours) { this.actualHours = actualHours; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public Boolean getIsOverdue() { return isOverdue; }
    public void setIsOverdue(Boolean isOverdue) { this.isOverdue = isOverdue; }

    // Audit fields
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = TicketStatus.OPEN;
        }
        if (priority == null) {
            priority = Priority.MEDIUM;
        }
        if (isOverdue == null) {
            isOverdue = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business methods
    public void assignTo(Long userId) {
        this.assignedToId = userId;
        this.status = TicketStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    public void resolve(String resolution) {
        this.status = TicketStatus.RESOLVED;
        this.resolution = resolution;
        this.resolvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.createdAt != null) {
            this.actualResolutionTime = java.time.Duration.between(this.createdAt, this.resolvedAt).toMinutes();
        }
    }

    public void close() {
        this.status = TicketStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void reopen() {
        this.status = TicketStatus.OPEN;
        this.resolvedAt = null;
        this.closedAt = null;
        this.resolution = null;
        this.actualResolutionTime = null;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOverdue() {
        if (slaDeadline == null || (status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED)) {
            return false;
        }
        return LocalDateTime.now().isAfter(slaDeadline);
    }

    public void updateSlaStatus() {
        this.isOverdue = isOverdue();
        this.updatedAt = LocalDateTime.now();
    }
}
