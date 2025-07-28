package com.todoapp.repository;

import com.todoapp.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Tag entity operations.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * Find tag by ID and user ID (for security).
     */
    Optional<Tag> findByIdAndUserId(Long id, Long userId);

    /**
     * Find all tags for a specific user.
     */
    List<Tag> findByUserIdOrderByNameAsc(Long userId);

    /**
     * Find tag by name and user.
     */
    Optional<Tag> findByNameAndUserId(String name, Long userId);

    /**
     * Check if tag exists by name and user.
     */
    boolean existsByNameAndUserId(String name, Long userId);

    /**
     * Find tags containing name (for autocomplete).
     */
    List<Tag> findByUserIdAndNameContainingIgnoreCaseOrderByNameAsc(Long userId, String nameContains);

    /**
     * Count tags for a user.
     */
    long countByUserId(Long userId);

    /**
     * Delete all tags for a user (for account deletion).
     */
    void deleteByUserId(Long userId);
} 