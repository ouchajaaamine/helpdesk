package com.example.ticketservice.service;

import com.example.ticketservice.entity.TicketCreateRequest;
import com.example.ticketservice.entity.TicketResponse;
import com.example.ticketservice.entity.TicketStatsResponse;
import com.example.ticketservice.entity.TicketUpdateRequest;
import com.example.ticketservice.entity.UserDTO;
import com.example.ticketservice.entity.Category;
import com.example.ticketservice.entity.Priority;
import com.example.ticketservice.entity.Ticket;
import com.example.ticketservice.entity.TicketStatus;
import com.example.ticketservice.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TicketService.class);
    private final TicketRepository ticketRepository;
    private final UserServiceClient userServiceClient;
    private final AuditService auditService;
    
    @Autowired
    public TicketService(TicketRepository ticketRepository, UserServiceClient userServiceClient, AuditService auditService) {
        this.ticketRepository = ticketRepository;
        this.userServiceClient = userServiceClient;
        this.auditService = auditService;
    }

    // Create a new ticket
    public TicketResponse createTicket(TicketCreateRequest ticketRequest) {
        log.info("Creating new ticket: {}", ticketRequest.getTitle());

        // Extract authenticated user information
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User must be authenticated to create a ticket");
        }

        // Get user ID from JWT token (you'll need to implement this based on your JWT structure)
        Long currentUserId = extractCurrentUserId();
        String currentUserRole = extractCurrentUserRole();

        log.info("Ticket created by user ID: {} with role: {}", currentUserId, currentUserRole);

        // Map request to entity
        Ticket ticket = new Ticket();
        ticket.setTitle(ticketRequest.getTitle());
        ticket.setDescription(ticketRequest.getDescription());
        ticket.setPriority(Priority.valueOf(ticketRequest.getPriority().toUpperCase()));
        ticket.setCategory(Category.valueOf(ticketRequest.getCategory().toUpperCase()));
        ticket.setEstimatedHours(ticketRequest.getEstimatedHours());

        // Set the creator
        ticket.setCreatedById(currentUserId);

        // Set default values
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        // Set default priority if not provided
        if (ticket.getPriority() == null) {
            ticket.setPriority(Priority.MEDIUM);
        }

        // Calculate SLA deadline based on priority
        setSlaDeadline(ticket);

        Ticket savedTicket = ticketRepository.save(ticket);

        // Log the ticket creation in audit trail
        auditService.logTicketCreation(savedTicket);

        return mapToResponse(savedTicket);
    }

    // Get ticket by ID
    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long id) {
        log.debug("Fetching ticket with ID: {}", id);
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found: " + id));
        return mapToResponse(ticket);
    }

    // Get all tickets
    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets() {
        log.debug("Fetching all tickets");
        return ticketRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // Get tickets by status
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByStatus(TicketStatus status) {
        log.debug("Fetching tickets with status: {}", status);
        return ticketRepository.findByStatus(status).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // Get tickets created by user
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByCreator(Long userId) {
        log.debug("Fetching tickets created by user: {}", userId);
        return ticketRepository.findByCreatedById(userId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // Get tickets assigned to technician
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByAssignee(Long technicianId) {
        log.debug("Fetching tickets assigned to technician: {}", technicianId);
        return ticketRepository.findByAssignedToId(technicianId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // Assign ticket to technician
    public TicketResponse assignTicket(Long ticketId, Long technicianId) {
        log.info("Assigning ticket {} to technician {}", ticketId, technicianId);

        // Validate technician exists and is active
        if (!userServiceClient.isValidTechnician(technicianId)) {
            throw new RuntimeException("Invalid technician ID: " + technicianId +
                ". Technician must exist and be active.");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

        // Capture old assignment for audit logging
        Long oldTechnicianId = ticket.getAssignedToId();

        ticket.setAssignedToId(technicianId);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updatedTicket = ticketRepository.save(ticket);

        // Log the ticket assignment in audit trail
        auditService.logTicketAssignment(updatedTicket, technicianId, "Technician-" + technicianId);

        return mapToResponse(updatedTicket);
    }

    // Update ticket status
    public TicketResponse updateTicketStatus(Long ticketId, TicketStatus newStatus) {
        log.info("Updating ticket {} status to {}", ticketId, newStatus);

        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

        // Capture old state for audit logging
        Ticket oldTicket = new Ticket();
        oldTicket.setStatus(ticket.getStatus());
        oldTicket.setTitle(ticket.getTitle());
        oldTicket.setDescription(ticket.getDescription());
        oldTicket.setPriority(ticket.getPriority());
        oldTicket.setCategory(ticket.getCategory());
        oldTicket.setAssignedToId(ticket.getAssignedToId());
        oldTicket.setResolution(ticket.getResolution());

        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());

        // Handle special status changes
        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
            if (ticket.getCreatedAt() != null) {
                long resolutionTime = java.time.Duration.between(ticket.getCreatedAt(), ticket.getResolvedAt()).toMinutes();
                ticket.setActualResolutionTime(resolutionTime);
            }
        } else if (newStatus == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        }

        Ticket updatedTicket = ticketRepository.save(ticket);

        // Log the status update in audit trail
        auditService.logTicketUpdate(oldTicket, updatedTicket);

        return mapToResponse(updatedTicket);
    }

    // Update ticket
    public TicketResponse updateTicket(Long ticketId, TicketUpdateRequest ticketRequest) {
        log.info("Updating ticket: {}", ticketId);

        Ticket existingTicket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

        // Capture old state for audit logging
        Ticket oldTicket = new Ticket();
        oldTicket.setTitle(existingTicket.getTitle());
        oldTicket.setDescription(existingTicket.getDescription());
        oldTicket.setPriority(existingTicket.getPriority());
        oldTicket.setCategory(existingTicket.getCategory());
        oldTicket.setAssignedToId(existingTicket.getAssignedToId());
        oldTicket.setResolution(existingTicket.getResolution());
        oldTicket.setStatus(existingTicket.getStatus());

        // Update fields
        existingTicket.setTitle(ticketRequest.getTitle());
        existingTicket.setDescription(ticketRequest.getDescription());
        existingTicket.setPriority(Priority.valueOf(ticketRequest.getPriority().toUpperCase()));
        existingTicket.setCategory(Category.valueOf(ticketRequest.getCategory().toUpperCase()));
        existingTicket.setEstimatedHours(ticketRequest.getEstimatedHours());
        existingTicket.setUpdatedAt(LocalDateTime.now());

        Ticket updatedTicket = ticketRepository.save(existingTicket);

        // Log the ticket update in audit trail
        auditService.logTicketUpdate(oldTicket, updatedTicket);

        return mapToResponse(updatedTicket);
    }

    // Delete ticket
    public void deleteTicket(Long ticketId) {
        log.info("Deleting ticket: {}", ticketId);

        // Get ticket before deletion for audit logging
        Ticket ticketToDelete = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

        // Log the deletion in audit trail before actually deleting
        auditService.logTicketDeletion(ticketToDelete);

        ticketRepository.deleteById(ticketId);
    }

    // Get open tickets
    @Transactional(readOnly = true)
    public List<TicketResponse> getOpenTickets() {
        return ticketRepository.findOpenTickets().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // Get overdue tickets
    @Transactional(readOnly = true)
    public List<TicketResponse> getOverdueTickets() {
        return ticketRepository.findOverdueTickets(LocalDateTime.now()).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // Get unassigned tickets
    @Transactional(readOnly = true)
    public List<TicketResponse> getUnassignedTickets() {
        return ticketRepository.findUnassignedTickets().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // Search tickets
    @Transactional(readOnly = true)
    public List<TicketResponse> searchTickets(String searchText) {
        log.debug("Searching tickets with text: {}", searchText);
        return ticketRepository.searchTickets(searchText).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // Get ticket statistics
    @Transactional(readOnly = true)
    public TicketStatsResponse getTicketStats() {
        long totalTickets = ticketRepository.count();
        long openTickets = ticketRepository.countByStatus(TicketStatus.OPEN);
        long inProgressTickets = ticketRepository.countByStatus(TicketStatus.IN_PROGRESS);
        long resolvedTickets = ticketRepository.countByStatus(TicketStatus.RESOLVED);
        long closedTickets = ticketRepository.countByStatus(TicketStatus.CLOSED);
        long overdueTickets = ticketRepository.findOverdueTickets(LocalDateTime.now()).size();

        TicketStatsResponse stats = new TicketStatsResponse();
        stats.setTotalTickets(totalTickets);
        stats.setOpenTickets(openTickets);
        stats.setInProgressTickets(inProgressTickets);
        stats.setResolvedTickets(resolvedTickets);
        stats.setClosedTickets(closedTickets);
        stats.setOverdueTickets(overdueTickets);
        return stats;
    }

    // Get available technicians for assignment
    @Transactional(readOnly = true)
    public List<UserDTO> getAvailableTechnicians() {
        log.debug("Fetching available technicians from user service");
        return userServiceClient.getTechnicians();
    }

    // Helper method to set SLA deadline based on priority
    private void setSlaDeadline(Ticket ticket) {
        LocalDateTime now = LocalDateTime.now();
        switch (ticket.getPriority()) {
            case URGENT:
                ticket.setSlaDeadline(now.plusHours(4)); // 4 hours
                break;
            case HIGH:
                ticket.setSlaDeadline(now.plusDays(1)); // 24 hours
                break;
            case MEDIUM:
                ticket.setSlaDeadline(now.plusDays(3)); // 3 days
                break;
            case LOW:
                ticket.setSlaDeadline(now.plusDays(7)); // 1 week
                break;
        }
    }

    // Extract current user ID from JWT token
    private Long extractCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // In a real implementation, you'd extract this from JWT claims
            // For now, we'll use a simple approach - you should implement proper JWT parsing
            String username = authentication.getName();
            // You'd typically have a user cache or call user-service to get user ID by username
            return getUserIdByUsername(username);
        }
        throw new RuntimeException("Unable to extract user ID from authentication");
    }

    // Extract current user role from JWT token
    private String extractCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().iterator().next().getAuthority();
        }
        return "EMPLOYEE"; // default role
    }

    // Helper method to get user ID by username (you'd implement this properly)
    public Long getUserIdByUsername(String username) {
        // In a real implementation, this would:
        // 1. Call user-service via Feign client
        // 2. Or use a local user cache
        // 3. Or extract from JWT claims

        // For demonstration, we'll use a simple mapping
        // You'd replace this with actual user-service integration
        switch (username) {
            case "john.admin@helpdesk.com": return 1L;
            case "sarah.manager@helpdesk.com": return 2L;
            case "alice.johnson@company.com": return 3L;
            default: return 999L; // fallback
        }
    }

    // Inner class for statistics
    public static class TicketStats {
        private final long totalTickets;
        private final long openTickets;
        private final long inProgressTickets;
        private final long resolvedTickets;
        private final long closedTickets;
        private final long overdueTickets;

        public TicketStats(long totalTickets, long openTickets, long inProgressTickets,
                          long resolvedTickets, long closedTickets, long overdueTickets) {
            this.totalTickets = totalTickets;
            this.openTickets = openTickets;
            this.inProgressTickets = inProgressTickets;
            this.resolvedTickets = resolvedTickets;
            this.closedTickets = closedTickets;
            this.overdueTickets = overdueTickets;
        }

        // Getters
        public long getTotalTickets() { return totalTickets; }
        public long getOpenTickets() { return openTickets; }
        public long getInProgressTickets() { return inProgressTickets; }
        public long getResolvedTickets() { return resolvedTickets; }
        public long getClosedTickets() { return closedTickets; }
        public long getOverdueTickets() { return overdueTickets; }
    }

    // Map Ticket entity to TicketResponse DTO
    private TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setPriority(ticket.getPriority());
        response.setCategory(ticket.getCategory());
        response.setEstimatedHours(ticket.getEstimatedHours());
        response.setStatus(ticket.getStatus());
        response.setCreatedById(ticket.getCreatedById());
        response.setAssignedToId(ticket.getAssignedToId());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        response.setSlaDeadline(ticket.getSlaDeadline());
        return response;
    }
}
