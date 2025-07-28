package com.todoapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health check controller for testing application status.
 * This controller provides additional health endpoints alongside Spring Boot Actuator.
 */
@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    private final HealthIndicator databaseHealthIndicator;

    public HealthController(HealthIndicator databaseHealthIndicator) {
        this.databaseHealthIndicator = databaseHealthIndicator;
    }

    @Operation(
        summary = "Application health check",
        description = "Returns the current health status of the application"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is healthy"),
        @ApiResponse(responseCode = "503", description = "Application is unhealthy")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "version", "1.0.0",
            "environment", "development",
            "message", "Application is running"
        ));
    }

    @Operation(
        summary = "Database health check",
        description = "Returns the current health status of the database connection"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Database is healthy"),
        @ApiResponse(responseCode = "503", description = "Database is unhealthy")
    })
    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> databaseHealth() {
        Health health = databaseHealthIndicator.health();
        
        Map<String, Object> response = Map.of(
            "database", health.getStatus().getCode(),
            "timestamp", LocalDateTime.now(),
            "details", health.getDetails()
        );
        
        return ResponseEntity.status(
            health.getStatus().getCode().equals("UP") ? 200 : 503
        ).body(response);
    }
} 