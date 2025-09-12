package com.example.ticketservice.repository;

import com.example.ticketservice.entity.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {

    // Find all attachments for a specific ticket
    List<FileAttachment> findByTicketIdOrderByUploadedAtDesc(Long ticketId);

    // Find attachments uploaded by a specific user
    List<FileAttachment> findByUploadedByOrderByUploadedAtDesc(Long uploadedBy);

    // Find attachments by file type (content type)
    List<FileAttachment> findByContentTypeStartingWithOrderByUploadedAtDesc(String contentTypePrefix);

    // Count total attachments for a ticket
    long countByTicketId(Long ticketId);

    // Find attachments by filename pattern
    @Query("SELECT f FROM FileAttachment f WHERE f.ticketId = :ticketId AND LOWER(f.fileName) LIKE LOWER(CONCAT('%', :fileName, '%'))")
    List<FileAttachment> findByTicketIdAndFileNameContainingIgnoreCase(@Param("ticketId") Long ticketId, @Param("fileName") String fileName);

    // Find large files (over a certain size)
    @Query("SELECT f FROM FileAttachment f WHERE f.fileSize > :minSize ORDER BY f.fileSize DESC")
    List<FileAttachment> findByFileSizeGreaterThan(@Param("minSize") Long minSize);

    // Find recent attachments across all tickets
    List<FileAttachment> findTop20ByOrderByUploadedAtDesc();
}
