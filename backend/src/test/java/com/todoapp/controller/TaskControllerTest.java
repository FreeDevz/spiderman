package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.dto.CreateTaskRequest;
import com.todoapp.dto.TaskDTO;
import com.todoapp.dto.UpdateTaskRequest;
import com.todoapp.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import com.todoapp.exception.GlobalExceptionHandler;
import com.todoapp.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerTest {

    @MockBean
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private TaskDTO testTaskDTO;
    private CreateTaskRequest createRequest;
    private UpdateTaskRequest updateRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        testTaskDTO = new TaskDTO();
        testTaskDTO.setId(1L);
        testTaskDTO.setTitle("Test Task");
        testTaskDTO.setDescription("Test Description");
        testTaskDTO.setStatus("pending");
        testTaskDTO.setPriority("medium");
        testTaskDTO.setDueDate(LocalDateTime.now().plusDays(1));

        createRequest = new CreateTaskRequest();
        createRequest.setTitle("New Task");
        createRequest.setDescription("New Description");
        createRequest.setPriority("high");

        updateRequest = new UpdateTaskRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPriority("low");
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void getTasks_ShouldReturnTasksPage() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TaskDTO> taskPage = new PageImpl<>(Arrays.asList(testTaskDTO), pageable, 1);
        when(taskService.getTasks("test@example.com", null, null, null, null, pageable)).thenReturn(taskPage);

        // When & Then
        mockMvc.perform(get("/api/tasks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(jsonPath("$.tasks[0].id").value(1))
                .andExpect(jsonPath("$.tasks[0].title").value("Test Task"))
                .andExpect(jsonPath("$.pagination.total").value(1));
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void getTasks_WithFilters_ShouldApplyFilters() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TaskDTO> taskPage = new PageImpl<>(Arrays.asList(testTaskDTO), pageable, 1);
        when(taskService.getTasks(eq("test@example.com"), eq("pending"), eq("high"), eq(1L), eq("test"), any(Pageable.class))).thenReturn(taskPage);

        // When & Then
        mockMvc.perform(get("/api/tasks")
                        .param("status", "pending")
                        .param("priority", "high")
                        .param("categoryId", "1")
                        .param("search", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(jsonPath("$.tasks[0].id").value(1));
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void createTask_WithValidRequest_ShouldReturnCreatedTask() throws Exception {
        // Given
        when(taskService.createTask(any(CreateTaskRequest.class), eq("test@example.com"))).thenReturn(testTaskDTO);

        // When & Then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void createTask_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateTaskRequest invalidRequest = new CreateTaskRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void getTask_WithValidId_ShouldReturnTask() throws Exception {
        // Given
        when(taskService.getTask(1L, "test@example.com")).thenReturn(testTaskDTO);

        // When & Then
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void getTask_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(taskService.getTask(999L, "test@example.com")).thenThrow(new ResourceNotFoundException("Task not found"));

        // When & Then
        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void updateTask_WithValidRequest_ShouldReturnUpdatedTask() throws Exception {
        // Given
        when(taskService.updateTask(eq(1L), any(UpdateTaskRequest.class), eq("test@example.com"))).thenReturn(testTaskDTO);

        // When & Then
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void updateTask_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(taskService.updateTask(eq(999L), any(UpdateTaskRequest.class), eq("test@example.com")))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        // When & Then
        mockMvc.perform(put("/api/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void deleteTask_WithValidId_ShouldReturnSuccess() throws Exception {
        // Given
        doNothing().when(taskService).deleteTask(1L, "test@example.com");

        // When & Then
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void deleteTask_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Task not found")).when(taskService).deleteTask(999L, "test@example.com");

        // When & Then
        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void updateTaskStatus_WithValidStatus_ShouldReturnUpdatedTask() throws Exception {
        // Given
        when(taskService.updateTaskStatus(1L, "COMPLETED", "test@example.com")).thenReturn(testTaskDTO);

        // When & Then
        mockMvc.perform(patch("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void updateTaskStatus_WithInvalidStatus_ShouldReturnBadRequest() throws Exception {
        // Given
        when(taskService.updateTaskStatus(1L, "INVALID_STATUS", "test@example.com"))
                .thenThrow(new IllegalArgumentException("Invalid status"));

        // When & Then
        mockMvc.perform(patch("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INVALID_STATUS\"}"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void bulkOperations_WithValidRequest_ShouldReturnSuccess() throws Exception {
        // Given
        Map<String, Object> bulkResult = new HashMap<>();
        bulkResult.put("deleted", 2);
        when(taskService.bulkOperations(any(), eq("test@example.com"))).thenReturn(bulkResult);

        // When & Then
        mockMvc.perform(post("/api/tasks/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskIds\":[1,2],\"operation\":\"delete\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(2));
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void exportTasks_WithValidFormat_ShouldReturnExportedData() throws Exception {
        // Given
        when(taskService.exportTasks("test@example.com", "json")).thenReturn("{\"tasks\":[]}");

        // When & Then
        mockMvc.perform(get("/api/tasks/export")
                        .param("format", "json"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"tasks\":[]}"));
    }

    @WithMockUser(username = "test@example.com")
    @Test
    void importTasks_WithValidData_ShouldReturnSuccess() throws Exception {
        // Given
        Map<String, Object> importResult = new HashMap<>();
        importResult.put("imported", 2);
        when(taskService.importTasks(anyString(), eq("json"), eq("test@example.com"))).thenReturn(importResult);

        // When & Then
        mockMvc.perform(post("/api/tasks/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("format", "json")
                        .content("{\"tasks\":[]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported").value(2));
    }

    @Test
    void getTasks_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createTask_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());
    }
} 