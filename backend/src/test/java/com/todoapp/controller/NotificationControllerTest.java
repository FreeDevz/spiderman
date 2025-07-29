package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.dto.NotificationDTO;
import com.todoapp.dto.NotificationSettingsDTO;
import com.todoapp.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getNotifications_ShouldReturnNotificationsList() throws Exception {
        // Given
        String userEmail = "test@example.com";
        List<NotificationDTO> notifications = Arrays.asList(
            createSampleNotificationDTO(1L, "Task Due", "Your task is due today", "info"),
            createSampleNotificationDTO(2L, "Task Completed", "Task marked as completed", "success"),
            createSampleNotificationDTO(3L, "Reminder", "Don't forget your meeting", "warning")
        );
        
        when(authentication.getName()).thenReturn(userEmail);
        when(notificationService.getNotifications(userEmail)).thenReturn(notifications);

        // When & Then
        mockMvc.perform(get("/api/notifications")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Task Due"))
                .andExpect(jsonPath("$[0].message").value("Your task is due today"))
                .andExpect(jsonPath("$[0].type").value("info"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Task Completed"))
                .andExpect(jsonPath("$[1].message").value("Task marked as completed"))
                .andExpect(jsonPath("$[1].type").value("success"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].title").value("Reminder"))
                .andExpect(jsonPath("$[2].message").value("Don't forget your meeting"))
                .andExpect(jsonPath("$[2].type").value("warning"));
    }

    @Test
    void markAsRead_ShouldReturnSuccessMessage() throws Exception {
        // Given
        String userEmail = "test@example.com";
        Long notificationId = 1L;
        
        when(authentication.getName()).thenReturn(userEmail);

        // When & Then
        mockMvc.perform(post("/api/notifications/{id}/read", notificationId)
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Notification marked as read"));
    }

    @Test
    void updateSettings_ShouldReturnUpdatedSettings() throws Exception {
        // Given
        String userEmail = "test@example.com";
        NotificationSettingsDTO request = createSampleNotificationSettingsDTO();
        request.setEmailNotifications(false);
        request.setPushNotifications(true);
        
        NotificationSettingsDTO updatedSettings = createSampleNotificationSettingsDTO();
        updatedSettings.setEmailNotifications(false);
        updatedSettings.setPushNotifications(true);
        
        when(authentication.getName()).thenReturn(userEmail);
        when(notificationService.updateSettings(any(NotificationSettingsDTO.class), anyString())).thenReturn(updatedSettings);

        // When & Then
        mockMvc.perform(put("/api/notifications/settings")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailNotifications").value(false))
                .andExpect(jsonPath("$.pushNotifications").value(true));
    }

    @Test
    void updateSettings_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        NotificationSettingsDTO request = new NotificationSettingsDTO();
        // Empty request will trigger validation errors
        
        when(authentication.getName()).thenReturn("test@example.com");

        // When & Then
        mockMvc.perform(put("/api/notifications/settings")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private NotificationDTO createSampleNotificationDTO(Long id, String title, String message, String type) {
        NotificationDTO notification = new NotificationDTO();
        notification.setId(id);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        return notification;
    }

    private NotificationSettingsDTO createSampleNotificationSettingsDTO() {
        NotificationSettingsDTO settings = new NotificationSettingsDTO();
        settings.setNotificationsEnabled(true);
        settings.setEmailNotifications(true);
        settings.setPushNotifications(false);
        settings.setTaskReminders(true);
        settings.setOverdueNotifications(true);
        settings.setDailyDigest(false);
        settings.setWeeklyReport(true);
        return settings;
    }
} 