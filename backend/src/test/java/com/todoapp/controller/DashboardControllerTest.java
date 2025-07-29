package com.todoapp.controller;

import com.todoapp.dto.DashboardStatisticsDTO;
import com.todoapp.dto.TaskDTO;
import com.todoapp.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DashboardController dashboardController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
    }

    @Test
    void getStatistics_ShouldReturnDashboardStatistics() throws Exception {
        // Given
        String userEmail = "test@example.com";
        DashboardStatisticsDTO statistics = createSampleDashboardStatistics();
        
        when(authentication.getName()).thenReturn(userEmail);
        when(dashboardService.getStatistics(userEmail)).thenReturn(statistics);

        // When & Then
        mockMvc.perform(get("/api/dashboard/statistics")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTasks").value(10))
                .andExpect(jsonPath("$.completedTasks").value(7))
                .andExpect(jsonPath("$.pendingTasks").value(3))
                .andExpect(jsonPath("$.completionRate").value(70.0))
                .andExpect(jsonPath("$.overdueTasks").value(1))
                .andExpect(jsonPath("$.todayTasks").value(2));
    }

    @Test
    void getTodayTasks_ShouldReturnTodayTasks() throws Exception {
        // Given
        String userEmail = "test@example.com";
        List<TaskDTO> todayTasks = Arrays.asList(
            createSampleTaskDTO(1L, "Today Task 1", "high"),
            createSampleTaskDTO(2L, "Today Task 2", "medium")
        );
        
        when(authentication.getName()).thenReturn(userEmail);
        when(dashboardService.getTodayTasks(userEmail)).thenReturn(todayTasks);

        // When & Then
        mockMvc.perform(get("/api/dashboard/today")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Today Task 1"))
                .andExpect(jsonPath("$[0].priority").value("high"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Today Task 2"))
                .andExpect(jsonPath("$[1].priority").value("medium"));
    }

    @Test
    void getUpcomingTasks_ShouldReturnUpcomingTasks() throws Exception {
        // Given
        String userEmail = "test@example.com";
        List<TaskDTO> upcomingTasks = Arrays.asList(
            createSampleTaskDTO(3L, "Upcoming Task 1", "high"),
            createSampleTaskDTO(4L, "Upcoming Task 2", "low"),
            createSampleTaskDTO(5L, "Upcoming Task 3", "medium")
        );
        
        when(authentication.getName()).thenReturn(userEmail);
        when(dashboardService.getUpcomingTasks(userEmail)).thenReturn(upcomingTasks);

        // When & Then
        mockMvc.perform(get("/api/dashboard/upcoming")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].title").value("Upcoming Task 1"))
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].title").value("Upcoming Task 2"))
                .andExpect(jsonPath("$[2].id").value(5))
                .andExpect(jsonPath("$[2].title").value("Upcoming Task 3"));
    }

    @Test
    void getOverdueTasks_ShouldReturnOverdueTasks() throws Exception {
        // Given
        String userEmail = "test@example.com";
        List<TaskDTO> overdueTasks = Arrays.asList(
            createSampleTaskDTO(6L, "Overdue Task 1", "high"),
            createSampleTaskDTO(7L, "Overdue Task 2", "medium")
        );
        
        when(authentication.getName()).thenReturn(userEmail);
        when(dashboardService.getOverdueTasks(userEmail)).thenReturn(overdueTasks);

        // When & Then
        mockMvc.perform(get("/api/dashboard/overdue")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(6))
                .andExpect(jsonPath("$[0].title").value("Overdue Task 1"))
                .andExpect(jsonPath("$[0].priority").value("high"))
                .andExpect(jsonPath("$[1].id").value(7))
                .andExpect(jsonPath("$[1].title").value("Overdue Task 2"))
                .andExpect(jsonPath("$[1].priority").value("medium"));
    }

    @Test
    void getRecentActivity_ShouldReturnRecentActivity() throws Exception {
        // Given
        String userEmail = "test@example.com";
        List<Map<String, Object>> activity = Arrays.asList(
            createSampleActivity("Task created", "New task added"),
            createSampleActivity("Task completed", "Task marked as done"),
            createSampleActivity("Category updated", "Category modified")
        );
        
        when(authentication.getName()).thenReturn(userEmail);
        when(dashboardService.getRecentActivity(userEmail)).thenReturn(activity);

        // When & Then
        mockMvc.perform(get("/api/dashboard/activity")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("Task created"))
                .andExpect(jsonPath("$[0].description").value("New task added"))
                .andExpect(jsonPath("$[1].action").value("Task completed"))
                .andExpect(jsonPath("$[1].description").value("Task marked as done"))
                .andExpect(jsonPath("$[2].action").value("Category updated"))
                .andExpect(jsonPath("$[2].description").value("Category modified"));
    }

    private DashboardStatisticsDTO createSampleDashboardStatistics() {
        DashboardStatisticsDTO statistics = new DashboardStatisticsDTO();
        statistics.setTotalTasks(10);
        statistics.setCompletedTasks(7);
        statistics.setPendingTasks(3);
        statistics.setCompletionRate(70.0);
        statistics.setOverdueTasks(1);
        statistics.setTodayTasks(2);
        statistics.setUpcomingTasks(5);
        statistics.setLastUpdated(LocalDateTime.now());
        return statistics;
    }

    private TaskDTO createSampleTaskDTO(Long id, String title, String priority) {
        TaskDTO task = new TaskDTO();
        task.setId(id);
        task.setTitle(title);
        task.setDescription("Sample task description");
        task.setPriority(priority);
        task.setStatus("pending");
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return task;
    }

    private Map<String, Object> createSampleActivity(String action, String description) {
        Map<String, Object> activity = new HashMap<>();
        activity.put("action", action);
        activity.put("description", description);
        activity.put("timestamp", LocalDateTime.now());
        return activity;
    }
} 