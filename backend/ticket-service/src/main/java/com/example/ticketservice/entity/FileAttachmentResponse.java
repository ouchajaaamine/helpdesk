package com.example.ticketservice.entity;

import java.time.LocalDateTime;

public class FileAttachmentResponse {

    private Long id;
    private Long ticketId;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private Long uploadedBy;
    private String uploadedByName;
    private String description;
    private LocalDateTime uploadedAt;

    // Default constructor
    public FileAttachmentResponse() {}

    // Constructor from FileAttachment entity
    public FileAttachmentResponse(FileAttachment attachment) {
        this.id = attachment.getId();
        this.ticketId = attachment.getTicketId();
        this.fileName = attachment.getFileName();
        this.fileSize = attachment.getFileSize();
        this.contentType = attachment.getContentType();
        this.uploadedBy = attachment.getUploadedBy();
        this.uploadedByName = attachment.getUploadedByName();
        this.description = attachment.getDescription();
        this.uploadedAt = attachment.getUploadedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

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
