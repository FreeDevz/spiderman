package com.todoapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO for UserSettings data transfer in API responses.
 */
public class UserSettingsDTO {
    
    private Long id;
    
    @NotNull(message = "Theme is required")
    private String theme; // LIGHT, DARK, AUTO
    
    @NotNull(message = "Notifications enabled status is required")
    private Boolean notificationsEnabled;
    
    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String timeZone;
    
    @Size(max = 5, message = "Language must not exceed 5 characters")
    private String language;
    
    @Size(max = 20, message = "Date format must not exceed 20 characters")
    private String dateFormat;
    
    @Size(max = 5, message = "Time format must not exceed 5 characters")
    private String timeFormat;
    
    private Boolean emailNotifications;
    private Boolean pushNotifications;
    private Boolean taskReminders;
    private Boolean dailyDigest;
    private Boolean weeklyReport;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserSettingsDTO() {}
    
    public UserSettingsDTO(Long id, String theme, Boolean notificationsEnabled) {
        this.id = id;
        this.theme = theme;
        this.notificationsEnabled = notificationsEnabled;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }
    
    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
    
        public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getDateFormat() {
        return dateFormat;
    }
    
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    public String getTimeFormat() {
        return timeFormat;
    }
    
    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
} 