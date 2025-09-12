package com.example.ticketservice.dto;

public class TicketCreateRequest {

    private String title;
    private String description;
    private String priority;
    private String category;
    private Long assignedToId;
    private Integer estimatedHours;

    public TicketCreateRequest() {}

    public TicketCreateRequest(String title, String description, String priority, 
                             String category, Long assignedToId, Integer estimatedHours) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.assignedToId = assignedToId;
        this.estimatedHours = estimatedHours;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getAssignedToId() { return assignedToId; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }

    public Integer getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; }
}
