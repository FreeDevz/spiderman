package com.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating new categories.
 */
public class CreateCategoryRequest {
    
    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "Category name must not exceed 50 characters")
    private String name;
    
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color code")
    private String color = "#3B82F6"; // Default blue color
    
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
    
    // Constructors
    public CreateCategoryRequest() {}
    
    public CreateCategoryRequest(String name) {
        this.name = name;
    }
    
    public CreateCategoryRequest(String name, String color) {
        this.name = name;
        this.color = color;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
} 