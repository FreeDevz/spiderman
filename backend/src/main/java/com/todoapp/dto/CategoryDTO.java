package com.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO for Category data transfer in API responses.
 */
public class CategoryDTO {
    
    private Long id;
    
    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "Category name must not exceed 50 characters")
    private String name;
    
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color code")
    private String color;
    
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long taskCount;
    private long completedTaskCount;
    private long pendingTaskCount;
    
    // Constructors
    public CategoryDTO() {}
    
    public CategoryDTO(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public long getTaskCount() {
        return taskCount;
    }
    
    public void setTaskCount(long taskCount) {
        this.taskCount = taskCount;
    }
    
    public long getCompletedTaskCount() {
        return completedTaskCount;
    }
    
    public void setCompletedTaskCount(long completedTaskCount) {
        this.completedTaskCount = completedTaskCount;
    }
    
    public long getPendingTaskCount() {
        return pendingTaskCount;
    }
    
    public void setPendingTaskCount(long pendingTaskCount) {
        this.pendingTaskCount = pendingTaskCount;
    }
} 