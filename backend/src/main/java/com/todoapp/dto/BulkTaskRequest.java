package com.todoapp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

/**
 * DTO for bulk task operations.
 */
public class BulkTaskRequest {
    
    @NotNull(message = "Operation type is required")
    @Pattern(regexp = "delete|update_status|move_category", message = "Operation must be delete, update_status, or move_category")
    private String operation;

    @NotEmpty(message = "Task IDs are required")
    private List<Long> taskIds;

    private String status; // For update_status operation
    private Long categoryId; // For move_category operation

    // Default constructor
    public BulkTaskRequest() {}

    public BulkTaskRequest(String operation, List<Long> taskIds, String status, Long categoryId) {
        this.operation = operation;
        this.taskIds = taskIds;
        this.status = status;
        this.categoryId = categoryId;
    }

    // Getters and setters
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
} 