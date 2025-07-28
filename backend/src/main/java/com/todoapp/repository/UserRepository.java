package com.todoapp.repository;

import com.todoapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email.
     */
    boolean existsByEmail(String email);

    /**
     * Find user by email and email verified status.
     */
    Optional<User> findByEmailAndEmailVerified(String email, Boolean emailVerified);

    /**
     * Find all verified users.
     */
    @Query("SELECT u FROM User u WHERE u.emailVerified = true")
    Iterable<User> findAllVerifiedUsers();

    /**
     * Count users by email verification status.
     */
    long countByEmailVerified(Boolean emailVerified);

    /**
     * Find user with their tasks loaded.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.tasks WHERE u.id = :userId")
    Optional<User> findByIdWithTasks(@Param("userId") Long userId);

    /**
     * Find user with their categories loaded.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.categories WHERE u.id = :userId")
    Optional<User> findByIdWithCategories(@Param("userId") Long userId);

    /**
     * Find user with user settings loaded.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userSettings WHERE u.id = :userId")
    Optional<User> findByIdWithSettings(@Param("userId") Long userId);
} 