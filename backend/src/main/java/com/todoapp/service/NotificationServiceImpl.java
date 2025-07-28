package com.todoapp.service;

import com.todoapp.dto.NotificationDTO;
import com.todoapp.dto.NotificationSettingsDTO;
import com.todoapp.entity.Notification;
import com.todoapp.entity.User;
import com.todoapp.entity.UserSettings;
import com.todoapp.repository.NotificationRepository;
import com.todoapp.repository.UserRepository;
import com.todoapp.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for notification operations.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, 
                                 UserRepository userRepository,
                                 UserSettingsRepository userSettingsRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
    }

    @Override
    public List<NotificationDTO> getNotifications(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return notifications.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public NotificationDTO markAsRead(Long notificationId, String userEmail) {
        User user = getUserByEmail(userEmail);
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, user.getId())
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.markAsRead();
        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    @Override
    public int markAllAsRead(String userEmail) {
        User user = getUserByEmail(userEmail);
        return notificationRepository.markAllAsRead(user.getId());
    }

    @Override
    public NotificationSettingsDTO getNotificationSettings(String userEmail) {
        User user = getUserByEmail(userEmail);
        UserSettings settings = userSettingsRepository.findByUserId(user.getId())
            .orElseGet(() -> createDefaultUserSettings(user));
        
        return convertToNotificationSettingsDTO(settings);
    }

    @Override
    public NotificationSettingsDTO updateSettings(NotificationSettingsDTO settingsDTO, String userEmail) {
        User user = getUserByEmail(userEmail);
        UserSettings settings = userSettingsRepository.findByUserId(user.getId())
            .orElseGet(() -> createDefaultUserSettings(user));
        
        // Update notification settings
        if (settingsDTO.getEmailNotifications() != null) {
            settings.setEmailNotifications(settingsDTO.getEmailNotifications());
        }
        if (settingsDTO.getPushNotifications() != null) {
            settings.setPushNotifications(settingsDTO.getPushNotifications());
        }
        if (settingsDTO.getTaskReminders() != null) {
            settings.setTaskReminders(settingsDTO.getTaskReminders());
        }
        if (settingsDTO.getDailyDigest() != null) {
            settings.setDailyDigest(settingsDTO.getDailyDigest());
        }
        if (settingsDTO.getWeeklyReport() != null) {
            settings.setWeeklyReport(settingsDTO.getWeeklyReport());
        }
        
        UserSettings savedSettings = userSettingsRepository.save(settings);
        return convertToNotificationSettingsDTO(savedSettings);
    }

    @Override
    public NotificationDTO createNotification(Long userId, String title, String message, String type) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Notification notification = new Notification(type, title, message, user);
        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserSettings createDefaultUserSettings(User user) {
        UserSettings settings = new UserSettings();
        settings.setUser(user);
        settings.setEmailNotifications(true);
        settings.setPushNotifications(true);
        settings.setTaskReminders(true);
        settings.setDailyDigest(false);
        settings.setWeeklyReport(true);
        return userSettingsRepository.save(settings);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.getRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setScheduledFor(notification.getScheduledFor());
        dto.setMetadata(notification.getMetadata());
        return dto;
    }

    private NotificationSettingsDTO convertToNotificationSettingsDTO(UserSettings settings) {
        NotificationSettingsDTO dto = new NotificationSettingsDTO();
        dto.setEmailNotifications(settings.getEmailNotifications());
        dto.setPushNotifications(settings.getPushNotifications());
        dto.setTaskReminders(settings.getTaskReminders());
        dto.setDailyDigest(settings.getDailyDigest());
        dto.setWeeklyReport(settings.getWeeklyReport());
        return dto;
    }
} 