package com.example.ticketservice.controller;

import com.example.ticketservice.entity.TicketCreateRequest;
import com.example.ticketservice.entity.TicketResponse;
import com.example.ticketservice.entity.TicketStatsResponse;
import com.example.ticketservice.entity.TicketUpdateRequest;
import com.example.ticketservice.entity.UserDTO;
import com.example.ticketservice.entity.Ticket;
import com.example.ticketservice.entity.TicketStatus;
import com.example.ticketservice.entity.Priority;
import com.example.ticketservice.entity.Category;
import com.example.ticketservice.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.example.ticketservice.entity.TicketAuditResponse;
import com.example.ticketservice.entity.TicketAudit;
import com.example.ticketservice.service.AuditService;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Ticket Management", description = "APIs for managing helpdesk tickets")
@SecurityRequirement(name = "bearerAuth")
public class TicketController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TicketController.class);
    private final TicketService ticketService;
    private final AuditService auditService;
    
    @Autowired
    public TicketController(TicketService ticketService, AuditService auditService) {
        this.ticketService = ticketService;
        this.auditService = auditService;
    }

    // Create a new ticket
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('TECHNICIAN')")
    @Operation(summary = "Create a new ticket", description = "Creates a new helpdesk ticket with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ticket created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    public ResponseEntity<TicketResponse> createTicket(@RequestBody TicketCreateRequest request) {
        log.info("Creating new ticket: {}", request.getTitle());

        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            log.info("Ticket creation requested by user: {}", currentUsername);

            // Service will automatically set createdById from authenticated user
            TicketResponse response = ticketService.createTicket(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error creating ticket", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Get all tickets
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN')")
    @Operation(summary = "Get all tickets", description = "Retrieves a list of all tickets in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        log.debug("Fetching all tickets");
        List<TicketResponse> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    // Get ticket by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('TECHNICIAN')")
    @Operation(summary = "Get ticket by ID", description = "Retrieves a specific ticket by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<TicketResponse> getTicketById(@Parameter(description = "ID of the ticket to retrieve") @PathVariable Long id) {
        log.debug("Fetching ticket with ID: {}", id);
        try {
            TicketResponse ticket = ticketService.getTicketById(id);
            // Check if user has permission to view this ticket
            if (hasPermissionToViewTicketResponse(ticket)) {
                return ResponseEntity.ok(ticket);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get audit trail for a specific ticket
    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('TECHNICIAN')")
    @Operation(summary = "Get ticket audit trail", description = "Retrieves the complete audit history for a specific ticket")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Audit trail retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<List<TicketAuditResponse>> getTicketAuditTrail(@Parameter(description = "ID of the ticket to get audit trail for") @PathVariable Long id) {
        log.debug("Fetching audit trail for ticket with ID: {}", id);
        try {
            // First check if user has permission to view the ticket
            TicketResponse ticket = ticketService.getTicketById(id);
            if (!hasPermissionToViewTicketResponse(ticket)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Get audit trail and convert to response DTOs
            List<TicketAudit> auditTrail = auditService.getAuditTrailForTicket(id);
            List<TicketAuditResponse> auditResponses = auditTrail.stream()
                .map(TicketAuditResponse::new)
                .collect(Collectors.toList());

            return ResponseEntity.ok(auditResponses);
        } catch (RuntimeException e) {
            log.error("Error fetching audit trail for ticket {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    // Get recent audit records across all tickets (Admin only)
    @GetMapping("/audit/recent")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get recent audit records", description = "Retrieves the most recent audit records across all tickets (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent audit records retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin access required")
    })
    public ResponseEntity<List<TicketAuditResponse>> getRecentAuditRecords() {
        log.debug("Fetching recent audit records");
        try {
            List<TicketAudit> recentAudits = auditService.getRecentAuditRecords();
            List<TicketAuditResponse> auditResponses = recentAudits.stream()
                .map(TicketAuditResponse::new)
                .collect(Collectors.toList());

            return ResponseEntity.ok(auditResponses);
        } catch (Exception e) {
            log.error("Error fetching recent audit records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get tickets by status
    @GetMapping("/status/{status}")
    @Operation(summary = "Get tickets by status", description = "Retrieves tickets filtered by their current status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    public ResponseEntity<List<TicketResponse>> getTicketsByStatus(@Parameter(description = "Status to filter tickets by") @PathVariable TicketStatus status) {
        log.debug("Fetching tickets with status: {}", status);
        List<TicketResponse> tickets = ticketService.getTicketsByStatus(status);
        return ResponseEntity.ok(tickets);
    }

    // Get tickets created by user
    @GetMapping("/creator/{userId}")
    @Operation(summary = "Get tickets by creator", description = "Retrieves tickets created by a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    public ResponseEntity<List<TicketResponse>> getTicketsByCreator(@Parameter(description = "ID of the user who created the tickets") @PathVariable Long userId) {
        log.debug("Fetching tickets created by user: {}", userId);
        List<TicketResponse> tickets = ticketService.getTicketsByCreator(userId);
        return ResponseEntity.ok(tickets);
    }

    // Get tickets assigned to technician
    @GetMapping("/assignee/{technicianId}")
    @Operation(summary = "Get tickets by assignee", description = "Retrieves tickets assigned to a specific technician")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    public ResponseEntity<List<TicketResponse>> getTicketsByAssignee(@Parameter(description = "ID of the technician assigned to the tickets") @PathVariable Long technicianId) {
        log.debug("Fetching tickets assigned to technician: {}", technicianId);
        List<TicketResponse> tickets = ticketService.getTicketsByAssignee(technicianId);
        return ResponseEntity.ok(tickets);
    }

    // Assign ticket to technician
    @PutMapping("/{ticketId}/assign/{technicianId}")
    @Operation(summary = "Assign ticket to technician", description = "Assigns a ticket to a specific technician")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket assigned successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Ticket or technician not found")
    })
    public ResponseEntity<TicketResponse> assignTicket(
            @Parameter(description = "ID of the ticket to assign") @PathVariable Long ticketId, 
            @Parameter(description = "ID of the technician to assign the ticket to") @PathVariable Long technicianId) {
        log.info("Assigning ticket {} to technician {}", ticketId, technicianId);
        try {
            TicketResponse ticket = ticketService.assignTicket(ticketId, technicianId);
            return ResponseEntity.ok(ticket);
        } catch (RuntimeException e) {
            log.error("Error assigning ticket", e);
            return ResponseEntity.notFound().build();
        }
    }

    // Update ticket status
    @PutMapping("/{ticketId}/status")
    @Operation(summary = "Update ticket status", description = "Updates the status of an existing ticket")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket status updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @Parameter(description = "ID of the ticket to update") @PathVariable Long ticketId, 
            @Parameter(description = "New status for the ticket") @RequestParam TicketStatus status) {
        log.info("Updating ticket {} status to {}", ticketId, status);
        try {
            TicketResponse ticket = ticketService.updateTicketStatus(ticketId, status);
            return ResponseEntity.ok(ticket);
        } catch (RuntimeException e) {
            log.error("Error updating ticket status", e);
            return ResponseEntity.notFound().build();
        }
    }

    // Update ticket
    @PutMapping("/{ticketId}")
    @Operation(summary = "Update ticket", description = "Updates an existing ticket with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<TicketResponse> updateTicket(
            @Parameter(description = "ID of the ticket to update") @PathVariable Long ticketId, 
            @RequestBody TicketUpdateRequest ticketRequest) {
        log.info("Updating ticket: {}", ticketId);
        try {
            TicketResponse updatedTicket = ticketService.updateTicket(ticketId, ticketRequest);
            return ResponseEntity.ok(updatedTicket);
        } catch (RuntimeException e) {
            log.error("Error updating ticket", e);
            return ResponseEntity.notFound().build();
        }
    }

    // Delete ticket
    @DeleteMapping("/{ticketId}")
    @Operation(summary = "Delete ticket", description = "Deletes a ticket from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Ticket deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<Void> deleteTicket(@Parameter(description = "ID of the ticket to delete") @PathVariable Long ticketId) {
        log.info("Deleting ticket: {}", ticketId);
        try {
            ticketService.deleteTicket(ticketId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting ticket", e);
            return ResponseEntity.notFound().build();
        }
    }

    // Get open tickets
    @GetMapping("/open")
    @Operation(summary = "Get open tickets", description = "Retrieves all tickets with OPEN status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Open tickets retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    public ResponseEntity<List<TicketResponse>> getOpenTickets() {
        List<TicketResponse> tickets = ticketService.getOpenTickets();
        return ResponseEntity.ok(tickets);
    }

    // Get overdue tickets
    @GetMapping("/overdue")
    @Operation(summary = "Get overdue tickets", description = "Retrieves all tickets that are past their SLA deadline")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Overdue tickets retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    public ResponseEntity<List<TicketResponse>> getOverdueTickets() {
        List<TicketResponse> tickets = ticketService.getOverdueTickets();
        return ResponseEntity.ok(tickets);
    }

    // Get unassigned tickets
    @GetMapping("/unassigned")
    @Operation(summary = "Get unassigned tickets", description = "Retrieves all tickets that are not assigned to any technician")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unassigned tickets retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    public ResponseEntity<List<TicketResponse>> getUnassignedTickets() {
        List<TicketResponse> tickets = ticketService.getUnassignedTickets();
        return ResponseEntity.ok(tickets);
    }

    // Search tickets
    @GetMapping("/search")
    @Operation(summary = "Search tickets", description = "Searches tickets based on a query string")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    public ResponseEntity<List<TicketResponse>> searchTickets(@Parameter(description = "Search query string") @RequestParam String query) {
        log.debug("Searching tickets with query: {}", query);
        List<TicketResponse> tickets = ticketService.searchTickets(query);
        return ResponseEntity.ok(tickets);
    }

    // Get ticket statistics
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN')")
    @Operation(summary = "Get ticket statistics", description = "Retrieves statistical information about tickets in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    public ResponseEntity<TicketStatsResponse> getTicketStats() {
        TicketStatsResponse stats = ticketService.getTicketStats();
        return ResponseEntity.ok(stats);
    }

    // Get available technicians for ticket assignment
    @GetMapping("/technicians")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN')")
    @Operation(summary = "Get available technicians", description = "Retrieves a list of available technicians for ticket assignment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Technicians retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    public ResponseEntity<List<UserDTO>> getAvailableTechnicians() {
        log.debug("Getting available technicians for ticket assignment");
        List<UserDTO> technicians = ticketService.getAvailableTechnicians();
        return ResponseEntity.ok(technicians);
    }

    // Helper method to convert Ticket entity to TicketResponse DTO
    private TicketResponse convertToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setStatus(ticket.getStatus());
        response.setPriority(ticket.getPriority());
        response.setCategory(ticket.getCategory());
        response.setCreatedById(ticket.getCreatedById());
        response.setAssignedToId(ticket.getAssignedToId());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        response.setResolvedAt(ticket.getResolvedAt());
        response.setClosedAt(ticket.getClosedAt());
        response.setSlaDeadline(ticket.getSlaDeadline());
        response.setActualResolutionTime(ticket.getActualResolutionTime());
        response.setEstimatedHours(ticket.getEstimatedHours());
        response.setActualHours(ticket.getActualHours());
        response.setResolution(ticket.getResolution());
        response.setIsOverdue(ticket.getIsOverdue());
        // Note: createdByName and assignedToName would be populated from user-service
        return response;
    }

    // Helper method to convert list of tickets to response DTOs
    private List<TicketResponse> convertToResponseList(List<Ticket> tickets) {
        return tickets.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Helper method to check if user has permission to view a ticket
    private boolean hasPermissionToViewTicket(Ticket ticket) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        String userRole = extractUserRole(authentication);

        // Admins can view all tickets
        if ("ADMIN".equals(userRole)) {
            return true;
        }

        // Technicians can view tickets assigned to them or unassigned tickets
        if ("TECHNICIAN".equals(userRole)) {
            return ticket.getAssignedToId() == null ||
                   ticket.getAssignedToId().equals(getCurrentUserId());
        }

        // Employees can only view tickets they created
        if ("EMPLOYEE".equals(userRole)) {
            return ticket.getCreatedById() != null &&
                   ticket.getCreatedById().equals(getCurrentUserId());
        }

        return false;
    }

    // Helper method to check if user has permission to view a ticket response
    private boolean hasPermissionToViewTicketResponse(TicketResponse ticket) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        String userRole = extractUserRole(authentication);

        // Admins can view all tickets
        if ("ADMIN".equals(userRole)) {
            return true;
        }

        // Technicians can view tickets assigned to them or unassigned tickets
        if ("TECHNICIAN".equals(userRole)) {
            return ticket.getAssignedToId() == null ||
                   ticket.getAssignedToId().equals(getCurrentUserId());
        }

        // Employees can only view tickets they created
        if ("EMPLOYEE".equals(userRole)) {
            return ticket.getCreatedById() != null &&
                   ticket.getCreatedById().equals(getCurrentUserId());
        }

        return false;
    }

    // Helper method to extract user role from authentication
    private String extractUserRole(Authentication authentication) {
        if (authentication.getAuthorities().iterator().hasNext()) {
            String authority = authentication.getAuthorities().iterator().next().getAuthority();
            return authority.replace("ROLE_", ""); // Remove ROLE_ prefix
        }
        return "EMPLOYEE"; // default
    }

    // Helper method to get current user ID
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // In a real implementation, you'd extract this from JWT claims
            return ticketService.getUserIdByUsername(authentication.getName());
        }
        return null;
    }
}
