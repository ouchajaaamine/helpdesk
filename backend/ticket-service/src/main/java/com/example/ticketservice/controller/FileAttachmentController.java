package com.example.ticketservice.controller;

import com.example.ticketservice.entity.FileAttachmentResponse;
import com.example.ticketservice.service.FileAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets/{ticketId}/attachments")
@Tag(name = "File Attachments", description = "APIs for managing file attachments on tickets")
@SecurityRequirement(name = "bearerAuth")
public class FileAttachmentController {

    @Autowired
    private FileAttachmentService fileAttachmentService;

    /**
     * Upload a file to a ticket
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('TECHNICIAN')")
    @Operation(summary = "Upload file to ticket", description = "Uploads a file and attaches it to the specified ticket")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file or request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<FileAttachmentResponse> uploadFile(
            @Parameter(description = "ID of the ticket to attach file to") @PathVariable Long ticketId,
            @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Optional description of the file") @RequestParam(value = "description", required = false) String description) {

        try {
            FileAttachmentResponse response = fileAttachmentService.uploadFile(ticketId, file, description);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(403).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    /**
     * Get all attachments for a ticket
     */
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('TECHNICIAN')")
    @Operation(summary = "Get ticket attachments", description = "Retrieves all file attachments for the specified ticket")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attachments retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<List<FileAttachmentResponse>> getAttachments(
            @Parameter(description = "ID of the ticket") @PathVariable Long ticketId) {

        try {
            List<FileAttachmentResponse> attachments = fileAttachmentService.getAttachmentsForTicket(ticketId);
            return ResponseEntity.ok(attachments);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(403).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    /**
     * Download a file attachment
     */
    @GetMapping("/{attachmentId}/download")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('TECHNICIAN')")
    @Operation(summary = "Download file attachment", description = "Downloads the specified file attachment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "File attachment not found")
    })
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "ID of the ticket") @PathVariable Long ticketId,
            @Parameter(description = "ID of the file attachment") @PathVariable Long attachmentId) {

        try {
            var attachment = fileAttachmentService.getAttachmentForDownload(attachmentId);

            Path filePath = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                       "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(resource);

        } catch (Exception e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(403).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    /**
     * Delete a file attachment
     */
    @DeleteMapping("/{attachmentId}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('TECHNICIAN')")
    @Operation(summary = "Delete file attachment", description = "Deletes the specified file attachment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "File attachment deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "File attachment not found")
    })
    public ResponseEntity<Void> deleteAttachment(
            @Parameter(description = "ID of the ticket") @PathVariable Long ticketId,
            @Parameter(description = "ID of the file attachment") @PathVariable Long attachmentId) {

        try {
            fileAttachmentService.deleteAttachment(attachmentId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(403).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    /**
     * Get attachment statistics for a ticket
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN') or hasRole('TECHNICIAN')")
    @Operation(summary = "Get attachment statistics", description = "Retrieves statistics about file attachments for the ticket")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<Map<String, Object>> getAttachmentStats(
            @Parameter(description = "ID of the ticket") @PathVariable Long ticketId) {

        try {
            var stats = fileAttachmentService.getAttachmentStats(ticketId);
            return ResponseEntity.ok(Map.of(
                "totalFiles", stats.getTotalFiles(),
                "totalSize", stats.getTotalSize(),
                "imageCount", stats.getImageCount(),
                "documentCount", stats.getDocumentCount()
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(403).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }
}
