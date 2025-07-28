package com.todoapp.repository;

import com.todoapp.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task entity operations.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find task by ID and user ID (for security).
     */
    Optional<Task> findByIdAndUserId(Long id, Long userId);

    /**
     * Find all tasks for a specific user.
     */
    Page<Task> findByUserId(Long userId, Pageable pageable);

    /**
     * Find tasks by user and status.
     */
    Page<Task> findByUserIdAndStatus(Long userId, Task.TaskStatus status, Pageable pageable);

    /**
     * Find tasks by user and priority.
     */
    Page<Task> findByUserIdAndPriority(Long userId, Task.TaskPriority priority, Pageable pageable);

    /**
     * Find tasks by user and category.
     */
    Page<Task> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);

    /**
     * Find tasks by user with search in title and description.
     */
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Task> findByUserIdAndSearch(@Param("userId") Long userId, 
                                   @Param("searchTerm") String searchTerm, 
                                   Pageable pageable);

    /**
     * Find overdue tasks for a user.
     */
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.status = 'PENDING' AND t.dueDate < :now")
    List<Task> findOverdueTasks(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * Find today's tasks for a user.
     */
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.status = 'PENDING' AND " +
           "DATE(t.dueDate) = DATE(:today)")
    List<Task> findTodaysTasks(@Param("userId") Long userId, @Param("today") LocalDateTime today);

    /**
     * Find upcoming tasks (next 7 days) for a user.
     */
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.status = 'PENDING' AND " +
           "t.dueDate BETWEEN :startDate AND :endDate ORDER BY t.dueDate ASC")
    List<Task> findUpcomingTasks(@Param("userId") Long userId, 
                               @Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);

    /**
     * Find tasks by user and tag.
     */
    @Query("SELECT t FROM Task t JOIN t.tags tag WHERE t.user.id = :userId AND tag.id = :tagId")
    Page<Task> findByUserIdAndTagId(@Param("userId") Long userId, 
                                  @Param("tagId") Long tagId, 
                                  Pageable pageable);

    /**
     * Count tasks by user and status.
     */
    long countByUserIdAndStatus(Long userId, Task.TaskStatus status);

    /**
     * Count overdue tasks for a user.
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.status = 'PENDING' AND t.dueDate < :now")
    long countOverdueTasks(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * Find tasks with their tags loaded.
     */
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.tags WHERE t.id = :taskId")
    Optional<Task> findByIdWithTags(@Param("taskId") Long taskId);

    /**
     * Find recent tasks for a user (last 30 days).
     */
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.createdAt >= :since ORDER BY t.createdAt DESC")
    List<Task> findRecentTasks(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    /**
     * Find completed tasks for a user within date range.
     */
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.status = 'COMPLETED' AND " +
           "t.completedAt BETWEEN :startDate AND :endDate ORDER BY t.completedAt DESC")
    List<Task> findCompletedTasksInRange(@Param("userId") Long userId,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Delete all tasks for a user (for account deletion).
     */
    void deleteByUserId(Long userId);
    
    /**
     * Count tasks by user ID.
     */
    long countByUserId(Long userId);
} 