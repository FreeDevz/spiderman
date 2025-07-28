package com.todoapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for creating a new task.
 */
@Schema(description = "Request object for creating a new task")
public class CreateTaskRequest {
    
    @Schema(description = "Task title", example = "Complete project documentation", required = true)
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Schema(description = "Task description", example = "Write comprehensive documentation for the project")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Schema(description = "Task priority", allowableValues = {"low", "medium", "high"}, example = "medium")
    @Pattern(regexp = "low|medium|high", message = "Priority must be low, medium, or high")
    private String priority = "medium";

    @Schema(description = "Task due date", example = "2024-12-31T23:59:59")
    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;

    @Schema(description = "Category ID for the task", example = "1")
    @Min(value = 1, message = "Category ID must be positive")
    private Long categoryId;

    @Schema(description = "List of tag names to create", example = "[\"urgent\", \"work\"]")
    private List<@NotBlank String> tags;
    
    @Schema(description = "List of existing tag IDs to associate", example = "[1, 2, 3]")
    private List<Long> tagIds;

    // Default constructor
    public CreateTaskRequest() {}

    public CreateTaskRequest(String title, String description, String priority, LocalDateTime dueDate, Long categoryId, List<String> tags) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.categoryId = categoryId;
        this.tags = tags;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
} 