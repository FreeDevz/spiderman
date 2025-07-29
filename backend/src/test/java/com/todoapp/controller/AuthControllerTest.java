package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.dto.AuthRequest;
import com.todoapp.dto.AuthResponse;
import com.todoapp.dto.RegisterRequest;
import com.todoapp.dto.UserDTO;
import com.todoapp.exception.GlobalExceptionHandler;
import com.todoapp.service.AuthService;
import com.todoapp.service.UserServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserServiceInterface userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private AuthRequest loginRequest;
    private RegisterRequest registerRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        loginRequest = new AuthRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        authResponse = new AuthResponse();
        authResponse.setToken("jwt-token");
        authResponse.setRefreshToken("refresh-token");
        authResponse.setTokenType("Bearer");
        authResponse.setExpiresIn(3600L);
        
        // Create a UserDTO for the response
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setName("Test User");
        userDTO.setEmailVerified(true);
        authResponse.setUser(userDTO);
    }

    @Test
    void register_WithValidRequest_ShouldReturnAuthResponse() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.name").value("Test User"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void register_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setPassword(""); // Empty password

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Service method should not be called for invalid requests
        // verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void login_WithValidRequest_ShouldReturnAuthResponse() throws Exception {
        // Given
        when(authService.login(any(AuthRequest.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.name").value("Test User"));

        verify(authService).login(any(AuthRequest.class));
    }

    @Test
    void login_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setPassword(""); // Empty password

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Service method should not be called for invalid requests
        // verify(authService).login(any(AuthRequest.class));
    }

    @Test
    void logout_ShouldReturnSuccessMessage() throws Exception {
        // Given
        // logout() is a void method, no need to mock return value

        // When & Then
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully logged out"));

        verify(authService).logout();
    }

    @Test
    void refreshToken_WithValidToken_ShouldReturnAuthResponse() throws Exception {
        // Given
        String refreshToken = "Bearer valid-refresh-token";
        when(authService.refreshToken("valid-refresh-token")).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.name").value("Test User"));

        verify(authService).refreshToken("valid-refresh-token");
    }

    @Test
    void refreshToken_WithInvalidToken_ShouldReturnBadRequest() throws Exception {
        // Given
        String invalidToken = "invalid-token";
        when(authService.refreshToken("invalid-token"))
                .thenThrow(new IllegalArgumentException("Invalid token format"));

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", invalidToken))
                .andExpect(status().isBadRequest());

        verify(authService).refreshToken("invalid-token");
    }

    @Test
    void refreshToken_WithoutAuthorizationHeader_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void forgotPassword_WithValidEmail_ShouldReturnSuccessMessage() throws Exception {
        // Given
        Map<String, String> request = Map.of("email", "test@example.com");
        // forgotPassword() is a void method, no need to mock return value

        // When & Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset email sent"));

        verify(authService).forgotPassword("test@example.com");
    }

    @Test
    void forgotPassword_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        // Given
        Map<String, String> request = Map.of("email", "invalid-email");
        doThrow(new IllegalArgumentException("Invalid email format"))
                .when(authService).forgotPassword("invalid-email");

        // When & Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService).forgotPassword("invalid-email");
    }

    @Test
    void forgotPassword_WithNonExistentEmail_ShouldReturnNotFound() throws Exception {
        // Given
        Map<String, String> request = Map.of("email", "nonexistent@example.com");
        doThrow(new RuntimeException("User not found"))
                .when(authService).forgotPassword("nonexistent@example.com");

        // When & Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(authService).forgotPassword("nonexistent@example.com");
    }

    @Test
    void resetPassword_WithValidRequest_ShouldReturnSuccessMessage() throws Exception {
        // Given
        Map<String, String> request = Map.of(
                "token", "valid-reset-token",
                "newPassword", "newPassword123"
        );
        // resetPassword() is a void method, no need to mock return value

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successful"));

        verify(authService).resetPassword("valid-reset-token", "newPassword123");
    }

    @Test
    void resetPassword_WithInvalidToken_ShouldReturnBadRequest() throws Exception {
        // Given
        Map<String, String> request = Map.of(
                "token", "invalid-token",
                "newPassword", "newPassword123"
        );
        doThrow(new IllegalArgumentException("Invalid token"))
                .when(authService).resetPassword("invalid-token", "newPassword123");

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService).resetPassword("invalid-token", "newPassword123");
    }

    @Test
    void resetPassword_WithWeakPassword_ShouldReturnBadRequest() throws Exception {
        // Given
        Map<String, String> request = Map.of(
                "token", "valid-token",
                "newPassword", "weak"
        );
        doThrow(new IllegalArgumentException("Password too weak"))
                .when(authService).resetPassword("valid-token", "weak");

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService).resetPassword("valid-token", "weak");
    }

    @Test
    void verifyEmail_WithValidToken_ShouldReturnSuccessMessage() throws Exception {
        // Given
        String token = "valid-verification-token";
        // verifyEmail() is a void method, no need to mock return value

        // When & Then
        mockMvc.perform(get("/api/auth/verify-email")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email verified successfully"));

        verify(authService).verifyEmail("valid-verification-token");
    }

    @Test
    void verifyEmail_WithInvalidToken_ShouldReturnBadRequest() throws Exception {
        // Given
        String token = "invalid-token";
        doThrow(new IllegalArgumentException("Invalid verification token"))
                .when(authService).verifyEmail("invalid-token");

        // When & Then
        mockMvc.perform(get("/api/auth/verify-email")
                .param("token", token))
                .andExpect(status().isBadRequest());

        verify(authService).verifyEmail("invalid-token");
    }

    @Test
    void verifyEmail_WithExpiredToken_ShouldReturnUnauthorized() throws Exception {
        // Given
        String token = "expired-token";
        doThrow(new RuntimeException("Token expired"))
                .when(authService).verifyEmail("expired-token");

        // When & Then
        mockMvc.perform(get("/api/auth/verify-email")
                .param("token", token))
                .andExpect(status().isUnauthorized());

        verify(authService).verifyEmail("expired-token");
    }

    @Test
    void verifyEmail_WithoutToken_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/verify-email"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_WithMissingRequiredFields_ShouldReturnBadRequest() throws Exception {
        // Given
        RegisterRequest incompleteRequest = new RegisterRequest();
        incompleteRequest.setName("Test User");
        // Missing email and password

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompleteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithMissingRequiredFields_ShouldReturnBadRequest() throws Exception {
        // Given
        AuthRequest incompleteRequest = new AuthRequest();
        incompleteRequest.setEmail("test@example.com");
        // Missing password

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompleteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void forgotPassword_WithMissingEmail_ShouldReturnBadRequest() throws Exception {
        // Given
        Map<String, String> request = Map.of(); // Empty request

        // When & Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPassword_WithMissingFields_ShouldReturnBadRequest() throws Exception {
        // Given
        Map<String, String> request = Map.of("token", "valid-token"); // Missing password

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
} 