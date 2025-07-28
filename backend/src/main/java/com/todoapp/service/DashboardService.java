package com.todoapp.service;

import com.todoapp.dto.DashboardStatisticsDTO;
import com.todoapp.dto.TaskDTO;

import java.util.List;
import java.util.Map;

/**
 * Service interface for dashboard operations.
 */
public interface DashboardService {
    
    /**
     * Get dashboard statistics for a user.
     * @param userEmail User email
     * @return Dashboard statistics
     */
    DashboardStatisticsDTO getStatistics(String userEmail);
    
    /**
     * Get today's tasks for a user.
     * @param userEmail User email
     * @return List of today's tasks
     */
    List<TaskDTO> getTodayTasks(String userEmail);
    
    /**
     * Get upcoming tasks for a user (next 7 days).
     * @param userEmail User email
     * @return List of upcoming tasks
     */
    List<TaskDTO> getUpcomingTasks(String userEmail);
    
    /**
     * Get overdue tasks for a user.
     * @param userEmail User email
     * @return List of overdue tasks
     */
    List<TaskDTO> getOverdueTasks(String userEmail);
    
    /**
     * Get recent activity for a user.
     * @param userEmail User email
     * @return Recent activity data
     */
    List<Map<String, Object>> getRecentActivity(String userEmail);
} 