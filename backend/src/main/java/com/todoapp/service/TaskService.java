package com.todoapp.service;

import com.todoapp.dto.TaskDTO;
import com.todoapp.dto.CreateTaskRequest;
import com.todoapp.dto.UpdateTaskRequest;
import com.todoapp.dto.BulkTaskRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * Service interface for task operations.
 */
public interface TaskService {
    
    /**
     * Get tasks for a user with optional filters.
     * @param userEmail User email
     * @param status Task status filter
     * @param priority Task priority filter
     * @param categoryId Category filter
     * @param search Search term
     * @param pageable Pagination
     * @return Page of tasks
     */
    Page<TaskDTO> getTasks(String userEmail, String status, String priority, Long categoryId, String search, Pageable pageable);
    
    /**
     * Create a new task.
     * @param request Task creation request
     * @param userEmail User email
     * @return Created task
     */
    TaskDTO createTask(CreateTaskRequest request, String userEmail);
    
    /**
     * Get a specific task.
     * @param id Task ID
     * @param userEmail User email
     * @return Task details
     */
    TaskDTO getTask(Long id, String userEmail);
    
    /**
     * Update a task.
     * @param id Task ID
     * @param request Task update request
     * @param userEmail User email
     * @return Updated task
     */
    TaskDTO updateTask(Long id, UpdateTaskRequest request, String userEmail);
    
    /**
     * Delete a task.
     * @param id Task ID
     * @param userEmail User email
     */
    void deleteTask(Long id, String userEmail);
    
    /**
     * Update task status.
     * @param id Task ID
     * @param status New status
     * @param userEmail User email
     * @return Updated task
     */
    TaskDTO updateTaskStatus(Long id, String status, String userEmail);
    
    /**
     * Perform bulk operations on tasks.
     * @param request Bulk operation request
     * @param userEmail User email
     * @return Operation result
     */
    Map<String, Object> bulkOperations(BulkTaskRequest request, String userEmail);
    
    /**
     * Export tasks.
     * @param userEmail User email
     * @param format Export format (json, csv, etc.)
     * @return Exported data
     */
    String exportTasks(String userEmail, String format);
    
    /**
     * Import tasks.
     * @param importData Import data
     * @param format Import format
     * @param userEmail User email
     * @return Import result
     */
    Map<String, Object> importTasks(String importData, String format, String userEmail);
} 