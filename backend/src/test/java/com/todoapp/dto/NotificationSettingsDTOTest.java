package com.todoapp.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NotificationSettingsDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        // When
        NotificationSettingsDTO settings = new NotificationSettingsDTO();

        // Then
        assertNotNull(settings);
        assertNull(settings.getNotificationsEnabled());
        assertTrue(settings.getEmailNotifications());
        assertTrue(settings.getPushNotifications());
        assertTrue(settings.getTaskReminders());
        assertTrue(settings.getOverdueNotifications());
        assertFalse(settings.getDailyDigest());
        assertFalse(settings.getWeeklyReport());
        assertNull(settings.getNotificationTypes());
    }

    @Test
    void testParameterizedConstructor() {
        // Given
        Boolean notificationsEnabled = true;

        // When
        NotificationSettingsDTO settings = new NotificationSettingsDTO(notificationsEnabled);

        // Then
        assertEquals(notificationsEnabled, settings.getNotificationsEnabled());
        assertTrue(settings.getEmailNotifications());
        assertTrue(settings.getPushNotifications());
        assertTrue(settings.getTaskReminders());
        assertTrue(settings.getOverdueNotifications());
        assertFalse(settings.getDailyDigest());
        assertFalse(settings.getWeeklyReport());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        NotificationSettingsDTO settings = new NotificationSettingsDTO();
        Boolean notificationsEnabled = false;
        Boolean emailNotifications = false;
        Boolean pushNotifications = true;
        Boolean taskReminders = false;
        Boolean overdueNotifications = true;
        Boolean dailyDigest = true;
        Boolean weeklyReport = false;
        Map<String, Boolean> notificationTypes = new HashMap<>();
        notificationTypes.put("task_created", true);
        notificationTypes.put("task_completed", false);

        // When
        settings.setNotificationsEnabled(notificationsEnabled);
        settings.setEmailNotifications(emailNotifications);
        settings.setPushNotifications(pushNotifications);
        settings.setTaskReminders(taskReminders);
        settings.setOverdueNotifications(overdueNotifications);
        settings.setDailyDigest(dailyDigest);
        settings.setWeeklyReport(weeklyReport);
        settings.setNotificationTypes(notificationTypes);

        // Then
        assertEquals(notificationsEnabled, settings.getNotificationsEnabled());
        assertEquals(emailNotifications, settings.getEmailNotifications());
        assertEquals(pushNotifications, settings.getPushNotifications());
        assertEquals(taskReminders, settings.getTaskReminders());
        assertEquals(overdueNotifications, settings.getOverdueNotifications());
        assertEquals(dailyDigest, settings.getDailyDigest());
        assertEquals(weeklyReport, settings.getWeeklyReport());
        assertEquals(notificationTypes, settings.getNotificationTypes());
    }

    @Test
    void testValidSettings() {
        // Given
        NotificationSettingsDTO settings = new NotificationSettingsDTO();
        settings.setNotificationsEnabled(true);
        settings.setEmailNotifications(true);
        settings.setPushNotifications(false);
        settings.setTaskReminders(true);
        settings.setOverdueNotifications(true);
        settings.setDailyDigest(false);
        settings.setWeeklyReport(true);

        // When
        Set<ConstraintViolation<NotificationSettingsDTO>> violations = validator.validate(settings);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullNotificationsEnabled() {
        // Given
        NotificationSettingsDTO settings = new NotificationSettingsDTO();
        settings.setNotificationsEnabled(null);

        // When
        Set<ConstraintViolation<NotificationSettingsDTO>> violations = validator.validate(settings);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("notificationsEnabled")));
    }

    @Test
    void testNotificationTypesMap() {
        // Given
        NotificationSettingsDTO settings = new NotificationSettingsDTO();
        Map<String, Boolean> notificationTypes = new HashMap<>();
        notificationTypes.put("task_created", true);
        notificationTypes.put("task_completed", false);
        notificationTypes.put("task_overdue", true);
        notificationTypes.put("daily_digest", false);

        // When
        settings.setNotificationTypes(notificationTypes);

        // Then
        assertEquals(notificationTypes, settings.getNotificationTypes());
        assertEquals(4, settings.getNotificationTypes().size());
        assertTrue(settings.getNotificationTypes().get("task_created"));
        assertFalse(settings.getNotificationTypes().get("task_completed"));
        assertTrue(settings.getNotificationTypes().get("task_overdue"));
        assertFalse(settings.getNotificationTypes().get("daily_digest"));
    }

    @Test
    void testDefaultValues() {
        // Given
        NotificationSettingsDTO settings = new NotificationSettingsDTO(true);

        // When & Then
        assertTrue(settings.getNotificationsEnabled());
        assertTrue(settings.getEmailNotifications());
        assertTrue(settings.getPushNotifications());
        assertTrue(settings.getTaskReminders());
        assertTrue(settings.getOverdueNotifications());
        assertFalse(settings.getDailyDigest());
        assertFalse(settings.getWeeklyReport());
    }

    @Test
    void testAllNotificationsDisabled() {
        // Given
        NotificationSettingsDTO settings = new NotificationSettingsDTO();
        settings.setNotificationsEnabled(false);
        settings.setEmailNotifications(false);
        settings.setPushNotifications(false);
        settings.setTaskReminders(false);
        settings.setOverdueNotifications(false);
        settings.setDailyDigest(false);
        settings.setWeeklyReport(false);

        // When
        Set<ConstraintViolation<NotificationSettingsDTO>> violations = validator.validate(settings);

        // Then
        assertTrue(violations.isEmpty()); // Should pass validation when notificationsEnabled is false
    }

    @Test
    void testAllNotificationsEnabled() {
        // Given
        NotificationSettingsDTO settings = new NotificationSettingsDTO();
        settings.setNotificationsEnabled(true);
        settings.setEmailNotifications(true);
        settings.setPushNotifications(true);
        settings.setTaskReminders(true);
        settings.setOverdueNotifications(true);
        settings.setDailyDigest(true);
        settings.setWeeklyReport(true);

        // When
        Set<ConstraintViolation<NotificationSettingsDTO>> violations = validator.validate(settings);

        // Then
        assertTrue(violations.isEmpty());
    }
} 