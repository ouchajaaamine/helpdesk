package com.example.ticketservice.repository;

import com.example.ticketservice.entity.TicketAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketAuditRepository extends JpaRepository<TicketAudit, Long> {

    // Find all audit records for a specific ticket
    List<TicketAudit> findByTicketIdOrderByChangedAtDesc(Long ticketId);

    // Find audit records for a ticket within a date range
    List<TicketAudit> findByTicketIdAndChangedAtBetweenOrderByChangedAtDesc(
        Long ticketId, LocalDateTime startDate, LocalDateTime endDate);

    // Find audit records by user who made the changes
    List<TicketAudit> findByChangedByOrderByChangedAtDesc(Long changedBy);

    // Find audit records by action type
    List<TicketAudit> findByActionOrderByChangedAtDesc(String action);

    // Find audit records for a specific field change
    List<TicketAudit> findByFieldNameOrderByChangedAtDesc(String fieldName);

    // Custom query to get audit summary for a ticket
    @Query("SELECT ta FROM TicketAudit ta WHERE ta.ticketId = :ticketId ORDER BY ta.changedAt DESC")
    List<TicketAudit> findAuditTrailForTicket(@Param("ticketId") Long ticketId);

    // Find recent audit records across all tickets
    List<TicketAudit> findTop50ByOrderByChangedAtDesc();

    // Count total audit records for a ticket
    long countByTicketId(Long ticketId);
}
