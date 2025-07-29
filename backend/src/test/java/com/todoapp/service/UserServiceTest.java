package com.todoapp.service;

import com.todoapp.dto.UserDTO;
import com.todoapp.dto.UserSettingsDTO;
import com.todoapp.entity.User;
import com.todoapp.entity.UserSettings;
import com.todoapp.exception.BusinessException;
import com.todoapp.exception.ResourceNotFoundException;
import com.todoapp.repository.UserRepository;
import com.todoapp.repository.UserSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService implementation.
 * Tests all user-related business logic including CRUD operations,
 * settings management, and validation.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSettingsRepository userSettingsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserSettings testUserSettings;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUserSettings = new UserSettings();
        testUserSettings.setId(1L);
        testUserSettings.setEmailNotifications(true);
        testUserSettings.setPushNotifications(false);
        testUserSettings.setTheme(UserSettings.Theme.DARK);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("encodedPassword");
        testUser.setName("Test User");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEnabled(true);
        testUser.setEmailVerified(true);
        testUser.setUserSettings(testUserSettings);

        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setName("Test User");
        testUserDTO.setFirstName("Test");
        testUserDTO.setLastName("User");
        testUserDTO.setEmailVerified(true);
    }

    @Test
    void findById_WithValidId_ShouldReturnUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_WithInvalidId_ShouldReturnEmpty() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findById(999L);
    }

    @Test
    void findByEmail_WithValidEmail_ShouldReturnUser() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByEmail("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void findByEmail_WithInvalidEmail_ShouldReturnEmpty() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void findAllUsers_ShouldReturnAllUsers() {
        // Given
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setName("User 2");
        
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        // When
        List<User> result = userService.findAllUsers();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("test@example.com");
        assertThat(result.get(1).getEmail()).isEqualTo("user2@example.com");
        verify(userRepository).findAll();
    }

    @Test
    void getUserProfile_WithValidEmail_ShouldReturnUserProfile() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        UserDTO result = userService.getUserProfile("test@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getUserByEmail_WithValidEmail_ShouldReturnUser() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        UserDTO result = userService.getUserByEmail("test@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void existsByEmail_WithExistingEmail_ShouldReturnTrue() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When
        boolean result = userService.existsByEmail("test@example.com");

        // Then
        assertThat(result).isTrue();
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void existsByEmail_WithNonExistingEmail_ShouldReturnFalse() {
        // Given
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // When
        boolean result = userService.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isFalse();
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }

    @Test
    void countUsers_ShouldReturnCorrectCount() {
        // Given
        when(userRepository.count()).thenReturn(5L);

        // When
        long result = userService.countUsers();

        // Then
        assertThat(result).isEqualTo(5L);
        verify(userRepository).count();
    }

    @Test
    void getUserSettings_WithValidEmail_ShouldReturnSettings() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userSettingsRepository.findByUserId(1L)).thenReturn(Optional.of(testUserSettings));

        // When
        UserSettingsDTO result = userService.getUserSettings("test@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmailNotifications()).isTrue();
        assertThat(result.getPushNotifications()).isFalse();
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void updateUserSettings_WithValidData_ShouldUpdateSettings() {
        // Given
        UserSettingsDTO settingsRequest = new UserSettingsDTO();
        settingsRequest.setEmailNotifications(false);
        settingsRequest.setPushNotifications(true);
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userSettingsRepository.findByUserId(1L)).thenReturn(Optional.of(testUserSettings));
        when(userSettingsRepository.save(any(UserSettings.class))).thenReturn(testUserSettings);

        // When
        UserSettingsDTO result = userService.updateUserSettings(settingsRequest, "test@example.com");

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).findByEmail("test@example.com");
        verify(userSettingsRepository).save(any(UserSettings.class));
    }
} 