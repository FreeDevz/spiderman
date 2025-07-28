package com.todoapp.service;

import com.todoapp.dto.DashboardStatisticsDTO;
import com.todoapp.dto.TaskDTO;
import com.todoapp.entity.Task;
import com.todoapp.entity.User;
import com.todoapp.repository.TaskRepository;
import com.todoapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Service implementation for dashboard operations.
 */
@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public DashboardServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DashboardStatisticsDTO getStatistics(String userEmail) {
        User user = getUserByEmail(userEmail);
        LocalDateTime now = LocalDateTime.now();
        
        DashboardStatisticsDTO stats = new DashboardStatisticsDTO();
        
        // Count tasks by status
        stats.setTotalTasks(taskRepository.countByUserId(user.getId()));
        stats.setPendingTasks(taskRepository.countByUserIdAndStatus(user.getId(), Task.TaskStatus.PENDING));
        stats.setCompletedTasks(taskRepository.countByUserIdAndStatus(user.getId(), Task.TaskStatus.COMPLETED));
        stats.setOverdueTasks(taskRepository.countOverdueTasks(user.getId(), now));
        
        // Calculate completion rate
        long totalTasks = stats.getTotalTasks();
        if (totalTasks > 0) {
            double completionRate = (double) stats.getCompletedTasks() / totalTasks * 100;
            stats.setCompletionRate(Math.round(completionRate * 100.0) / 100.0);
        } else {
            stats.setCompletionRate(0.0);
        }
        
        // Get today's tasks count
        List<Task> todayTasks = taskRepository.findTodaysTasks(user.getId(), now);
        stats.setTodayTasks(todayTasks.size());
        
        // Get upcoming tasks count (next 7 days)
        LocalDateTime startDate = LocalDate.now().atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(7);
        List<Task> upcomingTasks = taskRepository.findUpcomingTasks(user.getId(), startDate, endDate);
        stats.setUpcomingTasks(upcomingTasks.size());
        
        return stats;
    }

    @Override
    public List<TaskDTO> getTodayTasks(String userEmail) {
        User user = getUserByEmail(userEmail);
        LocalDateTime now = LocalDateTime.now();
        List<Task> tasks = taskRepository.findTodaysTasks(user.getId(), now);
        return tasks.stream()
            .map(this::convertToTaskDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getUpcomingTasks(String userEmail) {
        User user = getUserByEmail(userEmail);
        LocalDateTime startDate = LocalDate.now().atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(7);
        List<Task> tasks = taskRepository.findUpcomingTasks(user.getId(), startDate, endDate);
        return tasks.stream()
            .map(this::convertToTaskDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getOverdueTasks(String userEmail) {
        User user = getUserByEmail(userEmail);
        LocalDateTime now = LocalDateTime.now();
        List<Task> tasks = taskRepository.findOverdueTasks(user.getId(), now);
        return tasks.stream()
            .map(this::convertToTaskDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getRecentActivity(String userEmail) {
        User user = getUserByEmail(userEmail);
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        
        Map<String, Object> activity = new HashMap<>();
        
        // Get recent tasks
        List<Task> recentTasks = taskRepository.findRecentTasks(user.getId(), since);
        activity.put("recentTasks", recentTasks.stream()
            .map(this::convertToTaskDTO)
            .collect(Collectors.toList()));
        
        // Get completed tasks in last 7 days
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        List<Task> completedThisWeek = taskRepository.findCompletedTasksInRange(
            user.getId(), weekAgo, LocalDateTime.now());
        activity.put("completedThisWeek", completedThisWeek.size());
        
        // Get tasks created this week
        long createdThisWeek = recentTasks.stream()
            .filter(task -> task.getCreatedAt().isAfter(weekAgo))
            .count();
        activity.put("createdThisWeek", createdThisWeek);
        
        return List.of(activity);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private TaskDTO convertToTaskDTO(Task task) {
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
        
        return dto;
    }
} 