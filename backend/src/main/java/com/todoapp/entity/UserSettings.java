package com.todoapp.entity;

import jakarta.persistence.*;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * UserSettings entity for user preferences.
 * Maps to the 'user_settings' table in the database.
 */
@Entity
@Table(name = "user_settings")
@EntityListeners(AuditingEntityListener.class)
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Convert(converter = ThemeConverter.class)
    private Theme theme = Theme.LIGHT;

    @Column(name = "notifications_enabled", nullable = false)
    private Boolean notificationsEnabled = true;

    @Column(nullable = false, length = 50)
    private String timezone = "UTC";

    @Column(nullable = false, length = 5)
    private String language = "en";

    @Column(name = "date_format", nullable = false, length = 20)
    private String dateFormat = "MM/DD/YYYY";

    @Column(name = "time_format", nullable = false, length = 5)
    private String timeFormat = "12h";
    
    @Column(name = "email_notifications", nullable = false)
    private Boolean emailNotifications = true;
    
    @Column(name = "push_notifications", nullable = false)
    private Boolean pushNotifications = true;
    
    @Column(name = "task_reminders", nullable = false)
    private Boolean taskReminders = true;
    
    @Column(name = "daily_digest", nullable = false)
    private Boolean dailyDigest = false;
    
    @Column(name = "weekly_report", nullable = false)
    private Boolean weeklyReport = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Enums
    public enum Theme {
        LIGHT("light"), DARK("dark"), AUTO("auto");
        
        private final String value;
        
        Theme(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return value;
        }
    }

    // Constructors
    public UserSettings() {}

    public UserSettings(User user) {
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
    
    public String getTimeZone() {
        return timezone;
    }
    
    public void setTimeZone(String timeZone) {
        this.timezone = timeZone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSettings)) return false;
        UserSettings that = (UserSettings) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "id=" + id +
                ", theme=" + theme +
                ", notificationsEnabled=" + notificationsEnabled +
                ", timezone='" + timezone + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
    
    /**
     * Converter for Theme enum to handle lowercase database values
     */
    @Converter
    public static class ThemeConverter implements AttributeConverter<Theme, String> {
        
        @Override
        public String convertToDatabaseColumn(Theme theme) {
            return theme != null ? theme.getValue() : null;
        }
        
        @Override
        public Theme convertToEntityAttribute(String dbData) {
            if (dbData == null) {
                return Theme.LIGHT;
            }
            
            for (Theme theme : Theme.values()) {
                if (theme.getValue().equals(dbData)) {
                    return theme;
                }
            }
            
            return Theme.LIGHT; // Default fallback
        }
    }
} 