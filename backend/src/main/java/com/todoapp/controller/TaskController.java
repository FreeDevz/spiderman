package com.todoapp.controller;

import com.todoapp.dto.TaskDTO;
import com.todoapp.dto.CreateTaskRequest;
import com.todoapp.dto.UpdateTaskRequest;
import com.todoapp.dto.BulkTaskRequest;
import com.todoapp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

/**
 * Controller for Task operations.
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@Tag(name = "Tasks", description = "Task management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(
        summary = "Get all tasks",
        description = "Retrieves a paginated list of tasks with optional filtering by status, priority, category, and search term"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping
    public ResponseEntity<Page<TaskDTO>> getTasks(
            @Parameter(description = "Filter by task status (TODO, IN_PROGRESS, COMPLETED)")
            @RequestParam(required = false) String status,
            @Parameter(description = "Filter by priority (LOW, MEDIUM, HIGH)")
            @RequestParam(required = false) String priority,
            @Parameter(description = "Filter by category ID")
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Search term for task title or description")
            @RequestParam(required = false) String search,
            @Parameter(description = "Pagination and sorting parameters")
            Pageable pageable,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        Page<TaskDTO> tasks = taskService.getTasks(userEmail, status, priority, categoryId, search, pageable);
        return ResponseEntity.ok(tasks);
    }

    @Operation(
        summary = "Create a new task",
        description = "Creates a new task with the provided details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task created successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @Parameter(description = "Task creation details", required = true)
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        TaskDTO task = taskService.createTask(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @Operation(
        summary = "Get task by ID",
        description = "Retrieves a specific task by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable Long id,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        TaskDTO task = taskService.getTask(id, userEmail);
        return ResponseEntity.ok(task);
    }

    @Operation(
        summary = "Update task",
        description = "Updates an existing task with new information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Task update details", required = true)
            @Valid @RequestBody UpdateTaskRequest request,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        TaskDTO task = taskService.updateTask(id, request, userEmail);
        return ResponseEntity.ok(task);
    }

    @Operation(
        summary = "Delete task",
        description = "Deletes a task by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable Long id,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        taskService.deleteTask(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Update task status",
        description = "Updates the status of a specific task"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status updated successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @Parameter(description = "Task ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Status update request with new status", required = true)
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        String status = request.get("status");
        TaskDTO task = taskService.updateTaskStatus(id, status, userEmail);
        return ResponseEntity.ok(task);
    }

    @Operation(
        summary = "Bulk task operations",
        description = "Performs bulk operations on multiple tasks (create, update, delete)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bulk operations completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> bulkOperations(
            @Parameter(description = "Bulk operation details", required = true)
            @Valid @RequestBody BulkTaskRequest request,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        Map<String, Object> result = taskService.bulkOperations(request, userEmail);
        return ResponseEntity.ok(result);
    }

    @Operation(
        summary = "Export tasks",
        description = "Exports tasks in various formats (JSON, CSV, XML)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks exported successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/export")
    public ResponseEntity<String> exportTasks(
            @Parameter(description = "Export format (json, csv, xml)")
            @RequestParam(required = false) String format,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        String exportData = taskService.exportTasks(userEmail, format);
        return ResponseEntity.ok(exportData);
    }

    @Operation(
        summary = "Import tasks",
        description = "Imports tasks from various formats (JSON, CSV, XML)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks imported successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid format or data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importTasks(
            @Parameter(description = "Import data in specified format", required = true)
            @RequestBody String importData,
            @Parameter(description = "Import format (json, csv, xml)")
            @RequestParam(required = false) String format,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        Map<String, Object> result = taskService.importTasks(importData, format, userEmail);
        return ResponseEntity.ok(result);
    }
} 