package com.todoapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health check controller for testing application status.
 */
@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    @Operation(
        summary = "Application health check",
        description = "Returns the current health status of the application"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is healthy")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "version", "1.0.0",
            "environment", "development"
        ));
    }

    @Operation(
        summary = "Database health check",
        description = "Returns the current health status of the database connection"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Database is healthy")
    })
    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> databaseHealth() {
        // TODO: Add actual database health check
        return ResponseEntity.ok(Map.of(
            "database", "UP",
            "timestamp", LocalDateTime.now()
        ));
    }
} 