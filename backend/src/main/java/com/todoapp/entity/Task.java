package com.todoapp.entity;

import jakarta.persistence.*;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Task entity representing TODO items.
 * Maps to the 'tasks' table in the database.
 */
@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    @Convert(converter = TaskStatusConverter.class)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(nullable = false, length = 10)
    @Convert(converter = TaskPriorityConverter.class)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "task_tags",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // Constructors
    public Task() {}

    public Task(String title, User user) {
        this.title = title;
        this.user = user;
    }

    public Task(String title, String description, User user) {
        this.title = title;
        this.description = description;
        this.user = user;
    }

    // Enums
    public enum TaskStatus {
        PENDING("pending"), COMPLETED("completed"), DELETED("deleted");
        
        private final String value;
        
        TaskStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return value;
        }
    }

    public enum TaskPriority {
        LOW("low"), MEDIUM("medium"), HIGH("high");
        
        private final String value;
        
        TaskPriority(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return value;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        // Automatically set completedAt when task is marked as completed
        if (status == TaskStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        } else if (status != TaskStatus.COMPLETED) {
            this.completedAt = null;
        }
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    // Utility methods
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getTasks().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getTasks().remove(this);
    }

    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDateTime.now()) && status == TaskStatus.PENDING;
    }

    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }

    public boolean isPending() {
        return status == TaskStatus.PENDING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id != null && id.equals(task.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                '}';
    }
    
    /**
     * Converter for TaskPriority enum to handle lowercase database values
     */
    @Converter
    public static class TaskPriorityConverter implements AttributeConverter<TaskPriority, String> {
        
        @Override
        public String convertToDatabaseColumn(TaskPriority priority) {
            return priority != null ? priority.getValue() : null;
        }
        
        @Override
        public TaskPriority convertToEntityAttribute(String dbData) {
            if (dbData == null) {
                return TaskPriority.MEDIUM;
            }
            
            for (TaskPriority priority : TaskPriority.values()) {
                if (priority.getValue().equals(dbData)) {
                    return priority;
                }
            }
            
            return TaskPriority.MEDIUM; // Default fallback
        }
    }
    
    /**
     * Converter for TaskStatus enum to handle lowercase database values
     */
    @Converter
    public static class TaskStatusConverter implements AttributeConverter<TaskStatus, String> {
        
        @Override
        public String convertToDatabaseColumn(TaskStatus status) {
            return status != null ? status.getValue() : null;
        }
        
        @Override
        public TaskStatus convertToEntityAttribute(String dbData) {
            if (dbData == null) {
                return TaskStatus.PENDING;
            }
            
            for (TaskStatus status : TaskStatus.values()) {
                if (status.getValue().equals(dbData)) {
                    return status;
                }
            }
            
            return TaskStatus.PENDING; // Default fallback
        }
    }
} 