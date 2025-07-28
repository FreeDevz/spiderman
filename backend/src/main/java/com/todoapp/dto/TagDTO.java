package com.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO for Tag data transfer in API responses.
 */
public class TagDTO {
    
    private Long id;
    
    @NotBlank(message = "Tag name is required")
    @Size(max = 30, message = "Tag name must not exceed 30 characters")
    private String name;
    
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color code")
    private String color;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long taskCount;
    
    // Constructors
    public TagDTO() {}
    
    public TagDTO(Long id, String name) {
        this.id = id;
        this.name = name;
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
} 