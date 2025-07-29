package com.todoapp.service;

import com.todoapp.dto.DashboardStatisticsDTO;
import com.todoapp.dto.TaskDTO;
import com.todoapp.entity.Task;
import com.todoapp.entity.User;
import com.todoapp.repository.TaskRepository;
import com.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private User testUser;
    private Task pendingTask;
    private Task completedTask;
    private Task overdueTask;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        pendingTask = new Task();
        pendingTask.setId(1L);
        pendingTask.setTitle("Pending Task");
        pendingTask.setStatus(Task.TaskStatus.PENDING);
        pendingTask.setPriority(Task.TaskPriority.HIGH);
        pendingTask.setDueDate(LocalDateTime.now().plusDays(1));
        pendingTask.setUser(testUser);
        pendingTask.setCompletedAt(null);
        pendingTask.setCreatedAt(LocalDateTime.now());

        completedTask = new Task();
        completedTask.setId(2L);
        completedTask.setTitle("Completed Task");
        completedTask.setStatus(Task.TaskStatus.COMPLETED);
        completedTask.setPriority(Task.TaskPriority.MEDIUM);
        completedTask.setDueDate(LocalDateTime.now().plusDays(1));
        completedTask.setCompletedAt(LocalDateTime.now());
        completedTask.setUser(testUser);
        completedTask.setCreatedAt(LocalDateTime.now());

        overdueTask = new Task();
        overdueTask.setId(3L);
        overdueTask.setTitle("Overdue Task");
        overdueTask.setStatus(Task.TaskStatus.PENDING);
        overdueTask.setPriority(Task.TaskPriority.LOW);
        overdueTask.setDueDate(LocalDateTime.now().minusDays(1));
        overdueTask.setUser(testUser);
        overdueTask.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getDashboardStatistics_WithValidUser_ShouldReturnStatistics() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.countByUserId(1L)).thenReturn(10L);
        when(taskRepository.countByUserIdAndStatus(1L, Task.TaskStatus.COMPLETED)).thenReturn(5L);
        when(taskRepository.countByUserIdAndStatus(1L, Task.TaskStatus.PENDING)).thenReturn(3L);
        when(taskRepository.countOverdueTasks(eq(1L), any(LocalDateTime.class))).thenReturn(2L);

        // When
        DashboardStatisticsDTO statistics = dashboardService.getStatistics("test@example.com");

        // Then
        assertThat(statistics).isNotNull();
        assertThat(statistics.getTotalTasks()).isEqualTo(10);
        assertThat(statistics.getCompletedTasks()).isEqualTo(5);
        assertThat(statistics.getPendingTasks()).isEqualTo(3);
        assertThat(statistics.getOverdueTasks()).isEqualTo(2);
        assertThat(statistics.getCompletionRate()).isEqualTo(50.0); // 5/10 * 100

        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).countByUserId(1L);
        verify(taskRepository).countByUserIdAndStatus(1L, Task.TaskStatus.COMPLETED);
        verify(taskRepository).countByUserIdAndStatus(1L, Task.TaskStatus.PENDING);
        verify(taskRepository).countOverdueTasks(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getDashboardStatistics_WithNoTasks_ShouldReturnZeroStatistics() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.countByUserId(1L)).thenReturn(0L);
        when(taskRepository.countByUserIdAndStatus(1L, Task.TaskStatus.COMPLETED)).thenReturn(0L);
        when(taskRepository.countByUserIdAndStatus(1L, Task.TaskStatus.PENDING)).thenReturn(0L);
        when(taskRepository.countOverdueTasks(eq(1L), any(LocalDateTime.class))).thenReturn(0L);

        // When
        DashboardStatisticsDTO statistics = dashboardService.getStatistics("test@example.com");

        // Then
        assertThat(statistics).isNotNull();
        assertThat(statistics.getTotalTasks()).isEqualTo(0);
        assertThat(statistics.getCompletedTasks()).isEqualTo(0);
        assertThat(statistics.getPendingTasks()).isEqualTo(0);
        assertThat(statistics.getOverdueTasks()).isEqualTo(0);
        assertThat(statistics.getCompletionRate()).isEqualTo(0.0);
    }

    @Test
    void getDashboardStatistics_WithInvalidUser_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dashboardService.getStatistics("nonexistent@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void getTodaysTasks_WithValidUser_ShouldReturnTodaysTasks() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findTodaysTasks(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(pendingTask, completedTask));

        // When
        List<TaskDTO> todaysTasks = dashboardService.getTodayTasks("test@example.com");

        // Then
        assertThat(todaysTasks).isNotNull();
        assertThat(todaysTasks).hasSize(2);

        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findTodaysTasks(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getTodaysTasks_WithNoTasks_ShouldReturnEmptyList() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findTodaysTasks(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList());

        // When
        List<TaskDTO> todaysTasks = dashboardService.getTodayTasks("test@example.com");

        // Then
        assertThat(todaysTasks).isNotNull();
        assertThat(todaysTasks).isEmpty();

        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findTodaysTasks(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getUpcomingTasks_WithValidUser_ShouldReturnUpcomingTasks() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findUpcomingTasks(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(pendingTask));

        // When
        List<TaskDTO> upcomingTasks = dashboardService.getUpcomingTasks("test@example.com");

        // Then
        assertThat(upcomingTasks).isNotNull();
        assertThat(upcomingTasks).hasSize(1);

        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findUpcomingTasks(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getUpcomingTasks_WithNoTasks_ShouldReturnEmptyList() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findUpcomingTasks(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList());

        // When
        List<TaskDTO> upcomingTasks = dashboardService.getUpcomingTasks("test@example.com");

        // Then
        assertThat(upcomingTasks).isNotNull();
        assertThat(upcomingTasks).isEmpty();

        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findUpcomingTasks(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getOverdueTasks_WithValidUser_ShouldReturnOverdueTasks() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findOverdueTasks(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(overdueTask));

        // When
        List<TaskDTO> overdueTasks = dashboardService.getOverdueTasks("test@example.com");

        // Then
        assertThat(overdueTasks).isNotNull();
        assertThat(overdueTasks).hasSize(1);

        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findOverdueTasks(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getOverdueTasks_WithNoTasks_ShouldReturnEmptyList() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findOverdueTasks(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList());

        // When
        List<TaskDTO> overdueTasks = dashboardService.getOverdueTasks("test@example.com");

        // Then
        assertThat(overdueTasks).isNotNull();
        assertThat(overdueTasks).isEmpty();

        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findOverdueTasks(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getRecentActivity_WithValidUser_ShouldReturnRecentTasks() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findRecentTasks(eq(1L), any())).thenReturn(Arrays.asList(completedTask, pendingTask));
        when(taskRepository.findCompletedTasksInRange(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Arrays.asList(completedTask));

        // When
        List<Map<String, Object>> recentActivity = dashboardService.getRecentActivity("test@example.com");

        // Then
        assertThat(recentActivity).isNotNull();
        assertThat(recentActivity).hasSize(1);
        Map<String, Object> activity = recentActivity.get(0);
        assertThat((List<?>)activity.get("recentTasks")).hasSize(2);

        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findRecentTasks(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getRecentActivity_WithNoActivity_ShouldReturnEmptyList() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findRecentTasks(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList());

        // When
        List<Map<String, Object>> recentActivity = dashboardService.getRecentActivity("test@example.com");

        // Then
        assertThat(recentActivity).isNotNull();
        assertThat(recentActivity).hasSize(1);
        Map<String, Object> act = recentActivity.get(0);
        assertThat((List<?>)act.get("recentTasks")).isEmpty();

        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findRecentTasks(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void completionRateCalculation_ShouldBeCorrect() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.countByUserId(1L)).thenReturn(20L);
        when(taskRepository.countByUserIdAndStatus(1L, Task.TaskStatus.COMPLETED)).thenReturn(15L);
        when(taskRepository.countByUserIdAndStatus(1L, Task.TaskStatus.PENDING)).thenReturn(3L);
        when(taskRepository.countOverdueTasks(eq(1L), any(LocalDateTime.class))).thenReturn(2L);

        // When
        DashboardStatisticsDTO statistics = dashboardService.getStatistics("test@example.com");

        // Then
        assertThat(statistics.getCompletionRate()).isEqualTo(75.0); // 15/20 * 100
    }

    @Test
    void completionRateWithZeroTotalTasks_ShouldBeZero() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.countByUserId(1L)).thenReturn(0L);
        when(taskRepository.countByUserIdAndStatus(1L, Task.TaskStatus.COMPLETED)).thenReturn(0L);
        when(taskRepository.countByUserIdAndStatus(1L, Task.TaskStatus.PENDING)).thenReturn(0L);
        when(taskRepository.countOverdueTasks(eq(1L), any(LocalDateTime.class))).thenReturn(0L);

        // When
        DashboardStatisticsDTO statistics = dashboardService.getStatistics("test@example.com");

        // Then
        assertThat(statistics.getCompletionRate()).isEqualTo(0.0);
    }
} 