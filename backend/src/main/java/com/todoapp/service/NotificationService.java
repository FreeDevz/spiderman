package com.todoapp.service;

import com.todoapp.dto.NotificationDTO;
import com.todoapp.dto.NotificationSettingsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for notification operations.
 */
public interface NotificationService {
    
    /**
     * Get notifications for a user.
     * @param userEmail User email
     * @return List of notifications
     */
    List<NotificationDTO> getNotifications(String userEmail);
    
    /**
     * Mark a notification as read.
     * @param notificationId Notification ID
     * @param userEmail User email
     * @return Updated notification
     */
    NotificationDTO markAsRead(Long notificationId, String userEmail);
    
    /**
     * Mark all notifications as read for a user.
     * @param userEmail User email
     * @return Number of notifications marked as read
     */
    int markAllAsRead(String userEmail);
    
    /**
     * Get notification settings for a user.
     * @param userEmail User email
     * @return Notification settings
     */
    NotificationSettingsDTO getNotificationSettings(String userEmail);
    
    /**
     * Update notification settings for a user.
     * @param settings Notification settings
     * @param userEmail User email
     * @return Updated notification settings
     */
    NotificationSettingsDTO updateSettings(NotificationSettingsDTO settings, String userEmail);
    
    /**
     * Create a notification for a user.
     * @param userId User ID
     * @param title Notification title
     * @param message Notification message
     * @param type Notification type
     * @return Created notification
     */
    NotificationDTO createNotification(Long userId, String title, String message, String type);
} 