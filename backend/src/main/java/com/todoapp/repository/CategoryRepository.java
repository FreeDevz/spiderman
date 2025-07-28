package com.todoapp.repository;

import com.todoapp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Category entity operations.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by ID and user ID (for security).
     */
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    /**
     * Find all categories for a specific user.
     */
    List<Category> findByUserIdOrderByNameAsc(Long userId);

    /**
     * Find category by name and user.
     */
    Optional<Category> findByNameAndUserId(String name, Long userId);

    /**
     * Check if category exists by name and user.
     */
    boolean existsByNameAndUserId(String name, Long userId);

    /**
     * Find categories with task counts.
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.tasks WHERE c.user.id = :userId")
    List<Category> findByUserIdWithTasks(@Param("userId") Long userId);

    /**
     * Count categories for a user.
     */
    long countByUserId(Long userId);

    /**
     * Delete all categories for a user (for account deletion).
     */
    void deleteByUserId(Long userId);
} 