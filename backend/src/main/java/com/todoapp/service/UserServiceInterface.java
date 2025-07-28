package com.todoapp.service;

import com.todoapp.dto.UserDTO;
import com.todoapp.dto.UserSettingsDTO;
import com.todoapp.dto.UpdateUserRequest;
import com.todoapp.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user operations.
 */
public interface UserServiceInterface {
    
    /**
     * Find all users.
     * @return List of all users
     */
    List<User> findAllUsers();
    
    /**
     * Find user by ID.
     * @param id User ID
     * @return Optional user
     */
    Optional<User> findById(Long id);
    
    /**
     * Find user by email.
     * @param email User email
     * @return Optional user
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Count total users.
     * @return Total number of users
     */
    long countUsers();
    
    /**
     * Check if user exists by email.
     * @param email User email
     * @return True if user exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Get user profile.
     * @param userEmail User email
     * @return User profile DTO
     */
    UserDTO getUserProfile(String userEmail);
    
    /**
     * Update user profile.
     * @param request Update request
     * @param userEmail User email
     * @return Updated user profile
     */
    UserDTO updateUserProfile(UpdateUserRequest request, String userEmail);
    
    /**
     * Delete user account.
     * @param userEmail User email
     */
    void deleteUserAccount(String userEmail);
    
    /**
     * Get user settings.
     * @param userEmail User email
     * @return User settings
     */
    UserSettingsDTO getUserSettings(String userEmail);
    
    /**
     * Update user settings.
     * @param settings User settings
     * @param userEmail User email
     * @return Updated user settings
     */
    UserSettingsDTO updateUserSettings(UserSettingsDTO settings, String userEmail);
    
    /**
     * Get user by email (public method).
     * @param userEmail User email
     * @return User DTO
     */
    UserDTO getUserByEmail(String userEmail);
    
    /**
     * Update user profile.
     * @param request Update request
     * @param userEmail User email
     * @return Updated user DTO
     */
    UserDTO updateProfile(UpdateUserRequest request, String userEmail);
    
    /**
     * Delete user account.
     * @param userEmail User email
     */
    void deleteAccount(String userEmail);
} 