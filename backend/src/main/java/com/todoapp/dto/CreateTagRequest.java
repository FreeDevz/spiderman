package com.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating new tags.
 */
public class CreateTagRequest {
    
    @NotBlank(message = "Tag name is required")
    @Size(max = 30, message = "Tag name must not exceed 30 characters")
    private String name;
    
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color code")
    private String color = "#6B7280"; // Default gray color
    
    // Constructors
    public CreateTagRequest() {}
    
    public CreateTagRequest(String name) {
        this.name = name;
    }
    
    // Getters and Setters
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
} 