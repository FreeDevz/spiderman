package com.todoapp.repository;

import com.todoapp.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for UserSettings entity operations.
 */
@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {

    /**
     * Find user settings by user ID.
     */
    Optional<UserSettings> findByUserId(Long userId);

    /**
     * Check if user settings exist for a user.
     */
    boolean existsByUserId(Long userId);

    /**
     * Delete user settings for a user (for account deletion).
     */
    void deleteByUserId(Long userId);
} 