package com.example.ticketservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_attachments")
public class FileAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    // Keep ticketId for easy querying
    @Column(name = "ticket_id", nullable = false, insertable = false, updatable = false)
    private Long ticketId;

    @Column(name = "file_name", nullable = false)
    private String fileName; // Original filename

    @Column(name = "file_path", nullable = false)
    private String filePath; // Path where file is stored on disk

    @Column(name = "file_size", nullable = false)
    private Long fileSize; // Size in bytes

    @Column(name = "content_type", nullable = false)
    private String contentType; // MIME type (image/png, application/pdf, etc.)

    @Column(name = "uploaded_by", nullable = false)
    private Long uploadedBy; // User ID who uploaded the file

    @Column(name = "uploaded_by_name")
    private String uploadedByName; // User name for display

    @Column(name = "description")
    private String description; // Optional description of the file

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    // Default constructor
    public FileAttachment() {}

    // Constructor for creating new file attachments
    public FileAttachment(Ticket ticket, String fileName, String filePath,
                        Long fileSize, String contentType, Long uploadedBy,
                        String uploadedByName, String description) {
        this.ticket = ticket;
        this.ticketId = ticket.getId();
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.uploadedBy = uploadedBy;
        this.uploadedByName = uploadedByName;
        this.description = description;
        this.uploadedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }

    public String getUploadedByName() { return uploadedByName; }
    public void setUploadedByName(String uploadedByName) { this.uploadedByName = uploadedByName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
