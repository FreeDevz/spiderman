package com.todoapp.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for Dashboard statistics data transfer in API responses.
 */
public class DashboardStatisticsDTO {
    
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long overdueTasks;
    private long todayTasks;
    private long upcomingTasks;
    private double completionRate;
    
    private Map<String, Long> tasksByStatus;
    private Map<String, Long> tasksByPriority;
    private Map<String, Long> tasksByCategory;
    
    private List<TaskDTO> recentTasks;
    private List<TaskDTO> overdueTasksList;
    private List<TaskDTO> todayTasksList;
    private List<TaskDTO> upcomingTasksList;
    
    private LocalDateTime lastUpdated;
    
    // Constructors
    public DashboardStatisticsDTO() {}
    
    // Getters and Setters
    public long getTotalTasks() {
        return totalTasks;
    }
    
    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }
    
    public long getCompletedTasks() {
        return completedTasks;
    }
    
    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }
    
    public long getPendingTasks() {
        return pendingTasks;
    }
    
    public void setPendingTasks(long pendingTasks) {
        this.pendingTasks = pendingTasks;
    }
    
    public long getOverdueTasks() {
        return overdueTasks;
    }
    
    public void setOverdueTasks(long overdueTasks) {
        this.overdueTasks = overdueTasks;
    }
    
    public long getTodayTasks() {
        return todayTasks;
    }
    
    public void setTodayTasks(long todayTasks) {
        this.todayTasks = todayTasks;
    }
    
    public long getUpcomingTasks() {
        return upcomingTasks;
    }
    
    public void setUpcomingTasks(long upcomingTasks) {
        this.upcomingTasks = upcomingTasks;
    }
    
    public double getCompletionRate() {
        return completionRate;
    }
    
    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }
    
    public Map<String, Long> getTasksByStatus() {
        return tasksByStatus;
    }
    
    public void setTasksByStatus(Map<String, Long> tasksByStatus) {
        this.tasksByStatus = tasksByStatus;
    }
    
    public Map<String, Long> getTasksByPriority() {
        return tasksByPriority;
    }
    
    public void setTasksByPriority(Map<String, Long> tasksByPriority) {
        this.tasksByPriority = tasksByPriority;
    }
    
    public Map<String, Long> getTasksByCategory() {
        return tasksByCategory;
    }
    
    public void setTasksByCategory(Map<String, Long> tasksByCategory) {
        this.tasksByCategory = tasksByCategory;
    }
    
    public List<TaskDTO> getRecentTasks() {
        return recentTasks;
    }
    
    public void setRecentTasks(List<TaskDTO> recentTasks) {
        this.recentTasks = recentTasks;
    }
    
    public List<TaskDTO> getOverdueTasksList() {
        return overdueTasksList;
    }
    
    public void setOverdueTasksList(List<TaskDTO> overdueTasksList) {
        this.overdueTasksList = overdueTasksList;
    }
    
    public List<TaskDTO> getTodayTasksList() {
        return todayTasksList;
    }
    
    public void setTodayTasksList(List<TaskDTO> todayTasksList) {
        this.todayTasksList = todayTasksList;
    }
    
    public List<TaskDTO> getUpcomingTasksList() {
        return upcomingTasksList;
    }
    
    public void setUpcomingTasksList(List<TaskDTO> upcomingTasksList) {
        this.upcomingTasksList = upcomingTasksList;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
} 