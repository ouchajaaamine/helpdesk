package com.example.user_service.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User statistics for dashboard")
public class UserStats {
    @Schema(description = "Total number of users", example = "150")
    private int totalUsers;

    @Schema(description = "Number of pending users", example = "5")
    private int pendingUsers;

    @Schema(description = "Number of active users", example = "140")
    private int activeUsers;

    @Schema(description = "Number of admin users", example = "3")
    private int adminCount;

    @Schema(description = "Number of technician users", example = "20")
    private int technicianCount;

    @Schema(description = "Number of employee users", example = "117")
    private int employeeCount;

    public UserStats() {}

    public UserStats(int totalUsers, int pendingUsers, int activeUsers,
                    int adminCount, int technicianCount, int employeeCount) {
        this.totalUsers = totalUsers;
        this.pendingUsers = pendingUsers;
        this.activeUsers = activeUsers;
        this.adminCount = adminCount;
        this.technicianCount = technicianCount;
        this.employeeCount = employeeCount;
    }

    // Getters and Setters
    public int getTotalUsers() { return totalUsers; }
    public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

    public int getPendingUsers() { return pendingUsers; }
    public void setPendingUsers(int pendingUsers) { this.pendingUsers = pendingUsers; }

    public int getActiveUsers() { return activeUsers; }
    public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }

    public int getAdminCount() { return adminCount; }
    public void setAdminCount(int adminCount) { this.adminCount = adminCount; }

    public int getTechnicianCount() { return technicianCount; }
    public void setTechnicianCount(int technicianCount) { this.technicianCount = technicianCount; }

    public int getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(int employeeCount) { this.employeeCount = employeeCount; }
}
