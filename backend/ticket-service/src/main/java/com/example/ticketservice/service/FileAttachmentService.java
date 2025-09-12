package com.example.ticketservice.service;

import com.example.ticketservice.entity.FileAttachment;
import com.example.ticketservice.entity.FileAttachmentResponse;
import com.example.ticketservice.entity.Ticket;
import com.example.ticketservice.repository.FileAttachmentRepository;
import com.example.ticketservice.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileAttachmentService {

    @Autowired
    private FileAttachmentRepository fileAttachmentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // Base directory for file storage - configured in application.properties
    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    // Maximum file size in bytes (default 10MB)
    @Value("${file.max.size:10485760}")
    private Long maxFileSize;

    // Allowed file types
    private static final String[] ALLOWED_CONTENT_TYPES = {
        "image/jpeg", "image/png", "image/gif", "image/webp",
        "application/pdf",
        "text/plain", "text/csv",
        "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/zip", "application/x-rar-compressed"
    };

    /**
     * Upload a file and create attachment record
     */
    @Transactional
    public FileAttachmentResponse uploadFile(Long ticketId, MultipartFile file, String description) throws IOException {
        // Validate ticket exists and user has permission
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

        // Validate user permissions
        if (!hasPermissionToAccessTicket(ticket)) {
            throw new RuntimeException("Access denied: You don't have permission to upload files to this ticket");
        }

        // Validate file
        validateFile(file);

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, "tickets", ticketId.toString());
        Files.createDirectories(uploadPath);

        // Generate unique filename to avoid conflicts
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Save file to disk
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Get current user info
        Long currentUserId = getCurrentUserId();
        String currentUserName = getCurrentUserName();

        // Create attachment record
        FileAttachment attachment = new FileAttachment(
            ticket,
            originalFilename,
            filePath.toString(),
            file.getSize(),
            file.getContentType(),
            currentUserId,
            currentUserName,
            description
        );

        FileAttachment savedAttachment = fileAttachmentRepository.save(attachment);
        return new FileAttachmentResponse(savedAttachment);
    }

    /**
     * Get all attachments for a ticket
     */
    public List<FileAttachmentResponse> getAttachmentsForTicket(Long ticketId) {
        // Validate ticket exists and user has permission
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

        if (!hasPermissionToAccessTicket(ticket)) {
            throw new RuntimeException("Access denied: You don't have permission to view files for this ticket");
        }

        List<FileAttachment> attachments = fileAttachmentRepository.findByTicketIdOrderByUploadedAtDesc(ticketId);
        return attachments.stream()
            .map(FileAttachmentResponse::new)
            .collect(Collectors.toList());
    }

    /**
     * Download a file attachment
     */
    public FileAttachment getAttachmentForDownload(Long attachmentId) {
        FileAttachment attachment = fileAttachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new RuntimeException("File attachment not found: " + attachmentId));

        // Validate user has permission to access the ticket
        if (!hasPermissionToAccessTicket(attachment.getTicket())) {
            throw new RuntimeException("Access denied: You don't have permission to download this file");
        }

        return attachment;
    }

    /**
     * Delete a file attachment
     */
    @Transactional
    public void deleteAttachment(Long attachmentId) {
        FileAttachment attachment = fileAttachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new RuntimeException("File attachment not found: " + attachmentId));

        // Validate user has permission to access the ticket
        if (!hasPermissionToAccessTicket(attachment.getTicket())) {
            throw new RuntimeException("Access denied: You don't have permission to delete this file");
        }

        // Only allow deletion by the uploader or admin
        Long currentUserId = getCurrentUserId();
        String currentUserRole = getCurrentUserRole();

        if (!attachment.getUploadedBy().equals(currentUserId) && !"ADMIN".equals(currentUserRole)) {
            throw new RuntimeException("Access denied: Only the uploader or admin can delete this file");
        }

        // Delete physical file
        try {
            Path filePath = Paths.get(attachment.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log error but continue with database deletion
            System.err.println("Warning: Could not delete physical file: " + attachment.getFilePath());
        }

        // Delete database record
        fileAttachmentRepository.delete(attachment);
    }

    /**
     * Get attachment statistics for a ticket
     */
    public AttachmentStats getAttachmentStats(Long ticketId) {
        // Validate ticket exists and user has permission
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

        if (!hasPermissionToAccessTicket(ticket)) {
            throw new RuntimeException("Access denied: You don't have permission to view attachment stats for this ticket");
        }

        List<FileAttachment> attachments = fileAttachmentRepository.findByTicketIdOrderByUploadedAtDesc(ticketId);

        long totalFiles = attachments.size();
        long totalSize = attachments.stream().mapToLong(FileAttachment::getFileSize).sum();
        long imageCount = attachments.stream()
            .filter(a -> a.getContentType().startsWith("image/"))
            .count();
        long documentCount = attachments.stream()
            .filter(a -> a.getContentType().equals("application/pdf") ||
                        a.getContentType().contains("document") ||
                        a.getContentType().contains("text"))
            .count();

        return new AttachmentStats(totalFiles, totalSize, imageCount, documentCount);
    }

    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file) {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // Check file size
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("File size exceeds maximum allowed size: " + (maxFileSize / 1024 / 1024) + "MB");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new RuntimeException("File type not allowed: " + contentType);
        }

        // Check filename
        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new RuntimeException("Invalid filename");
        }

        // Check for dangerous file extensions
        if (containsDangerousExtension(filename)) {
            throw new RuntimeException("File type not allowed due to security restrictions");
        }
    }

    /**
     * Check if content type is allowed
     */
    private boolean isAllowedContentType(String contentType) {
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (allowedType.equals(contentType) || contentType.startsWith(allowedType.split("/")[0] + "/")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check for dangerous file extensions
     */
    private boolean containsDangerousExtension(String filename) {
        String[] dangerousExtensions = {".exe", ".bat", ".cmd", ".scr", ".pif", ".com", ".jar", ".js", ".vbs"};
        String lowerFilename = filename.toLowerCase();

        for (String ext : dangerousExtensions) {
            if (lowerFilename.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * Check if user has permission to access a ticket
     */
    private boolean hasPermissionToAccessTicket(Ticket ticket) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        String userRole = getCurrentUserRole();
        Long currentUserId = getCurrentUserId();

        // Admins can access all tickets
        if ("ADMIN".equals(userRole)) {
            return true;
        }

        // Technicians can access tickets assigned to them or unassigned tickets
        if ("TECHNICIAN".equals(userRole)) {
            return ticket.getAssignedToId() == null ||
                   ticket.getAssignedToId().equals(currentUserId);
        }

        // Employees can only access tickets they created
        if ("EMPLOYEE".equals(userRole)) {
            return ticket.getCreatedById() != null &&
                   ticket.getCreatedById().equals(currentUserId);
        }

        return false;
    }

    /**
     * Get current authenticated user ID
     */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String) {
            // In a real implementation, you'd extract user ID from JWT claims
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

    /**
     * Get current user role
     */
    private String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().iterator().hasNext()) {
            String authority = auth.getAuthorities().iterator().next().getAuthority();
            return authority.replace("ROLE_", "");
        }
        return "EMPLOYEE";
    }

    /**
     * Inner class for attachment statistics
     */
    public static class AttachmentStats {
        private final long totalFiles;
        private final long totalSize;
        private final long imageCount;
        private final long documentCount;

        public AttachmentStats(long totalFiles, long totalSize, long imageCount, long documentCount) {
            this.totalFiles = totalFiles;
            this.totalSize = totalSize;
            this.imageCount = imageCount;
            this.documentCount = documentCount;
        }

        public long getTotalFiles() { return totalFiles; }
        public long getTotalSize() { return totalSize; }
        public long getImageCount() { return imageCount; }
        public long getDocumentCount() { return documentCount; }
    }
}
