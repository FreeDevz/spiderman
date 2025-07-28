package com.todoapp.repository;

import com.todoapp.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Notification entity operations.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notification by ID and user ID (for security).
     */
    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    /**
     * Find all notifications for a specific user.
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find unread notifications for a user.
     */
    Page<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Count unread notifications for a user.
     */
    long countByUserIdAndReadFalse(Long userId);

    /**
     * Find notifications by type for a user.
     */
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type);

    /**
     * Find scheduled notifications that are due.
     */
    @Query("SELECT n FROM Notification n WHERE n.scheduledFor <= :now AND n.read = false")
    List<Notification> findDueNotifications(@Param("now") LocalDateTime now);

    /**
     * Mark all notifications as read for a user.
     */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user.id = :userId AND n.read = false")
    int markAllAsRead(@Param("userId") Long userId);

    /**
     * Mark a specific notification as read.
     */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.id = :id AND n.user.id = :userId")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * Delete old notifications (older than specified days).
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.user.id = :userId AND n.createdAt < :cutoffDate")
    int deleteOldNotifications(@Param("userId") Long userId, @Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Delete all notifications for a user (for account deletion).
     */
    void deleteByUserId(Long userId);
    
    /**
     * Find all notifications for a specific user without pagination.
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
} 