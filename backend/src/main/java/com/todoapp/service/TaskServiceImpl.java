package com.todoapp.service;

import com.todoapp.dto.TaskDTO;
import com.todoapp.dto.CreateTaskRequest;
import com.todoapp.dto.UpdateTaskRequest;
import com.todoapp.dto.BulkTaskRequest;
import com.todoapp.entity.Task;
import com.todoapp.entity.User;
import com.todoapp.entity.Category;
import com.todoapp.entity.Tag;
import com.todoapp.repository.TaskRepository;
import com.todoapp.repository.UserRepository;
import com.todoapp.repository.CategoryRepository;
import com.todoapp.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for task operations.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, 
                          UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          TagRepository tagRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public Page<TaskDTO> getTasks(String userEmail, String status, String priority, 
                                 Long categoryId, String search, Pageable pageable) {
        User user = getUserByEmail(userEmail);
        
        Page<Task> tasks;
        
        if (search != null && !search.trim().isEmpty()) {
            tasks = taskRepository.findByUserIdAndSearch(user.getId(), search.trim(), pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status.toUpperCase());
            tasks = taskRepository.findByUserIdAndStatus(user.getId(), taskStatus, pageable);
        } else if (priority != null && !priority.trim().isEmpty()) {
            Task.TaskPriority taskPriority = Task.TaskPriority.valueOf(priority.toUpperCase());
            tasks = taskRepository.findByUserIdAndPriority(user.getId(), taskPriority, pageable);
        } else if (categoryId != null) {
            tasks = taskRepository.findByUserIdAndCategoryId(user.getId(), categoryId, pageable);
        } else {
            tasks = taskRepository.findByUserId(user.getId(), pageable);
        }
        
        return tasks.map(this::convertToDTO);
    }

    @Override
    public TaskDTO createTask(CreateTaskRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(Task.TaskPriority.valueOf(request.getPriority().toUpperCase()));
        task.setDueDate(request.getDueDate());
        task.setUser(user);
        
        // Set category if provided
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            task.setCategory(category);
        }
        
        // Set tags if provided
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = tagRepository.findAllById(request.getTagIds())
                .stream()
                .collect(Collectors.toSet());
            task.setTags(tags);
        }
        
        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }

    @Override
    public TaskDTO getTask(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new RuntimeException("Task not found"));
        return convertToDTO(task);
    }

    @Override
    public TaskDTO updateTask(Long id, UpdateTaskRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(Task.TaskPriority.valueOf(request.getPriority().toUpperCase()));
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            task.setCategory(category);
        }
        if (request.getTagIds() != null) {
            Set<Tag> tags = tagRepository.findAllById(request.getTagIds())
                .stream()
                .collect(Collectors.toSet());
            task.setTags(tags);
        }
        
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    @Override
    public void deleteTask(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        task.setStatus(Task.TaskStatus.DELETED);
        taskRepository.save(task);
    }

    @Override
    public TaskDTO updateTaskStatus(Long id, String status, String userEmail) {
        User user = getUserByEmail(userEmail);
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        Task.TaskStatus newStatus = Task.TaskStatus.valueOf(status.toUpperCase());
        task.setStatus(newStatus);
        
        if (newStatus == Task.TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }
        
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    @Override
    public Map<String, Object> bulkOperations(BulkTaskRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);
        Map<String, Object> result = new HashMap<>();
        
        switch (request.getOperation().toUpperCase()) {
            case "DELETE":
                List<Task> tasksToDelete = taskRepository.findAllById(request.getTaskIds());
                tasksToDelete.forEach(task -> {
                    if (task.getUser().getId().equals(user.getId())) {
                        task.setStatus(Task.TaskStatus.DELETED);
                    }
                });
                taskRepository.saveAll(tasksToDelete);
                result.put("deleted", tasksToDelete.size());
                break;
                
            case "COMPLETE":
                List<Task> tasksToComplete = taskRepository.findAllById(request.getTaskIds());
                tasksToComplete.forEach(task -> {
                    if (task.getUser().getId().equals(user.getId())) {
                        task.setStatus(Task.TaskStatus.COMPLETED);
                        task.setCompletedAt(LocalDateTime.now());
                    }
                });
                taskRepository.saveAll(tasksToComplete);
                result.put("completed", tasksToComplete.size());
                break;
                
            case "MOVE_TO_CATEGORY":
                if (request.getCategoryId() == null) {
                    throw new RuntimeException("Category ID is required for move operation");
                }
                Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
                
                List<Task> tasksToMove = taskRepository.findAllById(request.getTaskIds());
                tasksToMove.forEach(task -> {
                    if (task.getUser().getId().equals(user.getId())) {
                        task.setCategory(category);
                    }
                });
                taskRepository.saveAll(tasksToMove);
                result.put("moved", tasksToMove.size());
                break;
                
            default:
                throw new RuntimeException("Unsupported bulk operation: " + request.getOperation());
        }
        
        return result;
    }

    @Override
    public String exportTasks(String userEmail, String format) {
        User user = getUserByEmail(userEmail);
        List<Task> tasks = taskRepository.findByUserId(user.getId(), Pageable.unpaged()).getContent();
        
        // Simple JSON export for now
        if ("json".equalsIgnoreCase(format)) {
            return tasks.stream()
                .map(this::convertToDTO)
                .map(this::taskDTOToJson)
                .collect(Collectors.joining(",", "[", "]"));
        }
        
        throw new RuntimeException("Unsupported export format: " + format);
    }

    @Override
    public Map<String, Object> importTasks(String importData, String format, String userEmail) {
        User user = getUserByEmail(userEmail);
        Map<String, Object> result = new HashMap<>();
        
        // Simple JSON import for now
        if ("json".equalsIgnoreCase(format)) {
            // This is a simplified implementation
            // In a real application, you'd want proper JSON parsing
            result.put("imported", 0);
            result.put("errors", Arrays.asList("Import functionality not fully implemented"));
        }
        
        return result;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus().name());
        dto.setPriority(task.getPriority().name());
        dto.setDueDate(task.getDueDate());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setOverdue(task.isOverdue());
        
        if (task.getCategory() != null) {
            dto.setCategoryId(task.getCategory().getId());
            dto.setCategoryName(task.getCategory().getName());
            dto.setCategoryColor(task.getCategory().getColor());
        }
        
        if (task.getTags() != null) {
            dto.setTags(task.getTags().stream()
                .map(this::convertTagToDTO)
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }

    private com.todoapp.dto.TagDTO convertTagToDTO(Tag tag) {
        com.todoapp.dto.TagDTO dto = new com.todoapp.dto.TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setColor(tag.getColor());
        return dto;
    }

    private String taskDTOToJson(TaskDTO dto) {
        // Simple JSON serialization
        return String.format(
            "{\"id\":%d,\"title\":\"%s\",\"status\":\"%s\",\"priority\":\"%s\"}",
            dto.getId(), dto.getTitle(), dto.getStatus(), dto.getPriority()
        );
    }
} 