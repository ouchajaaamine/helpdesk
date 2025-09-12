package com.example.ticketservice.dto;

public class TicketStatsResponse {

    private Long totalTickets;
    private Long openTickets;
    private Long inProgressTickets;
    private Long resolvedTickets;
    private Long closedTickets;
    private Long overdueTickets;
    private Double averageResolutionTime;
    private Long highPriorityTickets;
    private Long mediumPriorityTickets;
    private Long lowPriorityTickets;

    public TicketStatsResponse() {}

    public TicketStatsResponse(Long totalTickets, Long openTickets, Long inProgressTickets,
                             Long resolvedTickets, Long closedTickets, Long overdueTickets,
                             Double averageResolutionTime, Long highPriorityTickets,
                             Long mediumPriorityTickets, Long lowPriorityTickets) {
        this.totalTickets = totalTickets;
        this.openTickets = openTickets;
        this.inProgressTickets = inProgressTickets;
        this.resolvedTickets = resolvedTickets;
        this.closedTickets = closedTickets;
        this.overdueTickets = overdueTickets;
        this.averageResolutionTime = averageResolutionTime;
        this.highPriorityTickets = highPriorityTickets;
        this.mediumPriorityTickets = mediumPriorityTickets;
        this.lowPriorityTickets = lowPriorityTickets;
    }

    public Long getTotalTickets() { return totalTickets; }
    public void setTotalTickets(Long totalTickets) { this.totalTickets = totalTickets; }

    public Long getOpenTickets() { return openTickets; }
    public void setOpenTickets(Long openTickets) { this.openTickets = openTickets; }

    public Long getInProgressTickets() { return inProgressTickets; }
    public void setInProgressTickets(Long inProgressTickets) { this.inProgressTickets = inProgressTickets; }

    public Long getResolvedTickets() { return resolvedTickets; }
    public void setResolvedTickets(Long resolvedTickets) { this.resolvedTickets = resolvedTickets; }

    public Long getClosedTickets() { return closedTickets; }
    public void setClosedTickets(Long closedTickets) { this.closedTickets = closedTickets; }

    public Long getOverdueTickets() { return overdueTickets; }
    public void setOverdueTickets(Long overdueTickets) { this.overdueTickets = overdueTickets; }

    public Double getAverageResolutionTime() { return averageResolutionTime; }
    public void setAverageResolutionTime(Double averageResolutionTime) { this.averageResolutionTime = averageResolutionTime; }

    public Long getHighPriorityTickets() { return highPriorityTickets; }
    public void setHighPriorityTickets(Long highPriorityTickets) { this.highPriorityTickets = highPriorityTickets; }

    public Long getMediumPriorityTickets() { return mediumPriorityTickets; }
    public void setMediumPriorityTickets(Long mediumPriorityTickets) { this.mediumPriorityTickets = mediumPriorityTickets; }

    public Long getLowPriorityTickets() { return lowPriorityTickets; }
    public void setLowPriorityTickets(Long lowPriorityTickets) { this.lowPriorityTickets = lowPriorityTickets; }
}
