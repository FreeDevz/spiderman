package com.todoapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * DTO for notification settings data transfer.
 */
@Schema(description = "Notification settings data transfer object")
public class NotificationSettingsDTO {
    
    @NotNull(message = "Notifications enabled status is required")
    private Boolean notificationsEnabled;
    
    private Boolean emailNotifications = true;
    private Boolean pushNotifications = true;
    
    private Boolean taskReminders = true;
    private Boolean overdueNotifications = true;
    private Boolean dailyDigest = false;
    private Boolean weeklyReport = false;
    
    private Map<String, Boolean> notificationTypes; // Specific notification type settings
    
    // Constructors
    public NotificationSettingsDTO() {}
    
    public NotificationSettingsDTO(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
    
    // Getters and Setters
    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }
    
    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
    
    public Boolean getEmailNotifications() {
        return emailNotifications;
    }
    
    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }
    
    public Boolean getPushNotifications() {
        return pushNotifications;
    }
    
    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }
    
    public Boolean getTaskReminders() {
        return taskReminders;
    }
    
    public void setTaskReminders(Boolean taskReminders) {
        this.taskReminders = taskReminders;
    }
    
    public Boolean getOverdueNotifications() {
        return overdueNotifications;
    }
    
    public void setOverdueNotifications(Boolean overdueNotifications) {
        this.overdueNotifications = overdueNotifications;
    }
    
    public Boolean getDailyDigest() {
        return dailyDigest;
    }
    
    public void setDailyDigest(Boolean dailyDigest) {
        this.dailyDigest = dailyDigest;
    }
    
    public Boolean getWeeklyReport() {
        return weeklyReport;
    }
    
    public void setWeeklyReport(Boolean weeklyReport) {
        this.weeklyReport = weeklyReport;
    }
    
    public Map<String, Boolean> getNotificationTypes() {
        return notificationTypes;
    }
    
    public void setNotificationTypes(Map<String, Boolean> notificationTypes) {
        this.notificationTypes = notificationTypes;
    }
} 