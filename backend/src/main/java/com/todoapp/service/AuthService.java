package com.todoapp.service;

import com.todoapp.dto.AuthRequest;
import com.todoapp.dto.AuthResponse;
import com.todoapp.dto.RegisterRequest;

/**
 * Service interface for authentication operations.
 */
public interface AuthService {
    
    /**
     * Register a new user.
     * @param request Registration request
     * @return Authentication response with tokens
     */
    AuthResponse register(RegisterRequest request);
    
    /**
     * Authenticate a user.
     * @param request Authentication request
     * @return Authentication response with tokens
     */
    AuthResponse login(AuthRequest request);
    
    /**
     * Logout the current user.
     */
    void logout();
    
    /**
     * Refresh the access token using a refresh token.
     * @param refreshToken The refresh token
     * @return New authentication response
     */
    AuthResponse refreshToken(String refreshToken);
    
    /**
     * Send password reset email.
     * @param email User's email
     */
    void forgotPassword(String email);
    
    /**
     * Reset password using reset token.
     * @param token Reset token
     * @param newPassword New password
     */
    void resetPassword(String token, String newPassword);
    
    /**
     * Verify email using verification token.
     * @param token Verification token
     */
    void verifyEmail(String token);
} 