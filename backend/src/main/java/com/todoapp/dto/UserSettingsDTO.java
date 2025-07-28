package com.todoapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO for UserSettings data transfer in API responses.
 */
@Schema(description = "User settings data transfer object")
public class UserSettingsDTO {
    
    @Schema(description = "Unique identifier for user settings")
    private Long id;
    
    @Schema(description = "User interface theme", allowableValues = {"LIGHT", "DARK", "AUTO"}, example = "LIGHT")
    @NotNull(message = "Theme is required")
    private String theme; // LIGHT, DARK, AUTO
    
    @Schema(description = "Whether notifications are enabled", example = "true")
    @NotNull(message = "Notifications enabled status is required")
    private Boolean notificationsEnabled;
    
    @Schema(description = "User timezone", example = "UTC")
    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String timeZone;
    
    @Schema(description = "User language preference", example = "en")
    @Size(max = 5, message = "Language must not exceed 5 characters")
    private String language;
    
    @Schema(description = "Date format preference", example = "MM/dd/yyyy")
    @Size(max = 20, message = "Date format must not exceed 20 characters")
    private String dateFormat;
    
    @Schema(description = "Time format preference", example = "12h")
    @Size(max = 5, message = "Time format must not exceed 5 characters")
    private String timeFormat;
    
    @Schema(description = "Email notifications enabled", example = "true")
    private Boolean emailNotifications;
    @Schema(description = "Push notifications enabled", example = "true")
    private Boolean pushNotifications;
    @Schema(description = "Task reminders enabled", example = "true")
    private Boolean taskReminders;
    @Schema(description = "Daily digest enabled", example = "false")
    private Boolean dailyDigest;
    @Schema(description = "Weekly report enabled", example = "true")
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