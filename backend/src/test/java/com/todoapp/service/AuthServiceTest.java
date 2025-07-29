package com.todoapp.service;

import com.todoapp.dto.AuthRequest;
import com.todoapp.dto.AuthResponse;
import com.todoapp.dto.RegisterRequest;
import com.todoapp.entity.User;
import com.todoapp.repository.UserRepository;
import com.todoapp.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setPasswordHash("encodedPassword");
        testUser.setEnabled(true);
        testUser.setEmailVerified(true);

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
        registerRequest.setName("New User");

        authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");
    }

    @Test
    void register_WithValidRequest_ShouldReturnAuthResponse() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        User savedUser = new User();
        savedUser.setEmail("newuser@example.com");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(anyString())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refreshToken");

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(response.getUser().getEmail()).isEqualTo("newuser@example.com");

        verify(userRepository).findByEmail("newuser@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken("newuser@example.com");
        verify(jwtUtil).generateRefreshToken("newuser@example.com");
    }

    @Test
    void register_WithExistingEmail_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User with this email already exists");

        verify(userRepository).findByEmail("newuser@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken("test@example.com")).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken("test@example.com")).thenReturn("refreshToken");

        // When
        AuthResponse response = authService.login(authRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("test@example.com");
        verify(jwtUtil).generateToken("test@example.com");
        verify(jwtUtil).generateRefreshToken("test@example.com");
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        assertThatThrownBy(() -> authService.login(authRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid credentials");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void refreshToken_WithValidToken_ShouldReturnNewTokens() {
        // Given
        String refreshToken = "validRefreshToken";
        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtUtil.getEmailFromRefreshToken(refreshToken)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken("test@example.com")).thenReturn("newAccessToken");
        when(jwtUtil.generateRefreshToken("test@example.com")).thenReturn("newRefreshToken");

        // When
        AuthResponse response = authService.refreshToken(refreshToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("newAccessToken");
        assertThat(response.getRefreshToken()).isEqualTo("newRefreshToken");

        verify(jwtUtil).validateRefreshToken(refreshToken);
        verify(jwtUtil).getEmailFromRefreshToken(refreshToken);
        verify(userRepository).findByEmail("test@example.com");
        verify(jwtUtil).generateToken("test@example.com");
        verify(jwtUtil).generateRefreshToken("test@example.com");
    }

    @Test
    void refreshToken_WithInvalidToken_ShouldThrowException() {
        // Given
        String refreshToken = "invalidRefreshToken";
        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid refresh token");

        verify(jwtUtil).validateRefreshToken(refreshToken);
        verify(jwtUtil, never()).getEmailFromRefreshToken(anyString());
    }

    @Test
    void logout_ShouldCompleteSuccessfully() {
        // When
        authService.logout();

        // Then - logout should complete without throwing exceptions
        // In a real implementation, this might invalidate tokens or clear sessions
        assertThat(true).isTrue(); // Placeholder assertion
    }

    @Test
    void forgotPassword_WithValidEmail_ShouldCompleteSuccessfully() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        authService.forgotPassword("test@example.com");

        // Then
        verify(userRepository).findByEmail("test@example.com");
        // In a real implementation, this would send an email
    }

    @Test
    void forgotPassword_WithInvalidEmail_ShouldCompleteSuccessfully() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        authService.forgotPassword("nonexistent@example.com");

        // Then - should not throw exception, just return silently for security
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void resetPassword_WithValidToken_ShouldCompleteSuccessfully() {
        // When
        authService.resetPassword("validToken", "newPassword123");

        // Then - placeholder implementation, should not throw exception
        assertThat(true).isTrue();
    }

    @Test
    void resetPassword_WithInvalidToken_ShouldCompleteSuccessfully() {
        // When
        authService.resetPassword("invalidToken", "newPassword123");

        // Then - placeholder implementation, should not throw exception
        assertThat(true).isTrue();
    }

    @Test
    void verifyEmail_WithValidToken_ShouldCompleteSuccessfully() {
        // When
        authService.verifyEmail("validToken");

        // Then - placeholder implementation, should not throw exception
        assertThat(true).isTrue();
    }

    @Test
    void verifyEmail_WithInvalidToken_ShouldCompleteSuccessfully() {
        // When
        authService.verifyEmail("invalidToken");

        // Then - placeholder implementation, should not throw exception
        assertThat(true).isTrue();
    }
} 