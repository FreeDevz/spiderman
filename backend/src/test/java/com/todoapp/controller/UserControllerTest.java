package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.dto.UpdateUserRequest;
import com.todoapp.dto.UserDTO;
import com.todoapp.dto.UserSettingsDTO;
import com.todoapp.service.UserService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register JSR310 module for LocalDateTime
    }

    @Test
    void getProfile_ShouldReturnUserProfile() throws Exception {
        // Given
        String userEmail = "test@example.com";
        UserDTO userDTO = createSampleUserDTO();
        
        when(authentication.getName()).thenReturn(userEmail);
        when(userService.getUserByEmail(userEmail)).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(get("/api/users/profile")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDTO.getId()))
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()))
                .andExpect(jsonPath("$.name").value(userDTO.getName()));
    }

    @Test
    void updateProfile_ShouldReturnUpdatedUser() throws Exception {
        // Given
        String userEmail = "test@example.com";
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Updated Name");
        request.setFirstName("Updated");
        request.setLastName("Name");
        
        UserDTO updatedUser = createSampleUserDTO();
        updatedUser.setName("Updated Name");
        
        when(authentication.getName()).thenReturn(userEmail);
        when(userService.updateProfile(any(UpdateUserRequest.class), anyString())).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/users/profile")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void updateProfile_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("a".repeat(101)); // Name too long to trigger validation
        
        when(authentication.getName()).thenReturn("test@example.com");

        // When & Then
        mockMvc.perform(put("/api/users/profile")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAccount_ShouldReturnSuccessMessage() throws Exception {
        // Given
        String userEmail = "test@example.com";
        
        when(authentication.getName()).thenReturn(userEmail);

        // When & Then
        mockMvc.perform(delete("/api/users/account")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account deleted successfully"));
    }

    @Test
    void getSettings_ShouldReturnUserSettings() throws Exception {
        // Given
        String userEmail = "test@example.com";
        UserSettingsDTO settings = createSampleUserSettingsDTO();
        
        when(authentication.getName()).thenReturn(userEmail);
        when(userService.getUserSettings(userEmail)).thenReturn(settings);

        // When & Then
        mockMvc.perform(get("/api/users/settings")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theme").value(settings.getTheme()))
                .andExpect(jsonPath("$.language").value(settings.getLanguage()));
    }

    @Test
    void updateSettings_ShouldReturnUpdatedSettings() throws Exception {
        // Given
        String userEmail = "test@example.com";
        UserSettingsDTO request = createSampleUserSettingsDTO();
        request.setTheme("dark");
        
        UserSettingsDTO updatedSettings = createSampleUserSettingsDTO();
        updatedSettings.setTheme("dark");
        
        when(authentication.getName()).thenReturn(userEmail);
        when(userService.updateUserSettings(any(UserSettingsDTO.class), anyString())).thenReturn(updatedSettings);

        // When & Then
        mockMvc.perform(put("/api/users/settings")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theme").value("dark"));
    }

    @Test
    void updateSettings_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        UserSettingsDTO request = new UserSettingsDTO();
        request.setNotificationsEnabled(null); // Null value to trigger validation
        
        when(authentication.getName()).thenReturn("test@example.com");

        // When & Then
        mockMvc.perform(put("/api/users/settings")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserCount_ShouldReturnUserCount() throws Exception {
        // Given
        long userCount = 5L;
        when(userService.countUsers()).thenReturn(userCount);

        // When & Then
        mockMvc.perform(get("/api/users/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userCount").value(userCount))
                .andExpect(jsonPath("$.message").value("Database connection successful"));
    }

    @Test
    void testDatabase_Success_ShouldReturnSuccessStatus() throws Exception {
        // Given
        long userCount = 5L;
        when(userService.countUsers()).thenReturn(userCount);

        // When & Then
        mockMvc.perform(get("/api/users/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.userCount").value(userCount))
                .andExpect(jsonPath("$.message").value("Database connection and query successful"));
    }

    @Test
    void testDatabase_Exception_ShouldReturnErrorStatus() throws Exception {
        // Given
        when(userService.countUsers()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/users/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Database connection failed: Database error"));
    }

    private UserDTO createSampleUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setName("Test User");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");
        userDTO.setCreatedAt(LocalDateTime.now());
        userDTO.setUpdatedAt(LocalDateTime.now());
        return userDTO;
    }

    private UserSettingsDTO createSampleUserSettingsDTO() {
        UserSettingsDTO settings = new UserSettingsDTO();
        settings.setId(1L);
        settings.setTheme("LIGHT");
        settings.setNotificationsEnabled(true);
        settings.setLanguage("en");
        settings.setEmailNotifications(true);
        settings.setPushNotifications(false);
        settings.setCreatedAt(LocalDateTime.now());
        settings.setUpdatedAt(LocalDateTime.now());
        return settings;
    }
} 