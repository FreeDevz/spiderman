package com.todoapp.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom health indicator for database connectivity.
 * Provides detailed database health information including connection status,
 * response time, and basic database statistics.
 */
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        
        try {
            // Test basic connectivity
            long startTime = System.currentTimeMillis();
            String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
            long responseTime = System.currentTimeMillis() - startTime;
            
            if ("1".equals(result)) {
                details.put("status", "UP");
                details.put("response_time_ms", responseTime);
                details.put("message", "Database connection is healthy");
                
                // Add additional database statistics
                addDatabaseStats(details);
                
                return Health.up()
                    .withDetails(details)
                    .build();
            } else {
                details.put("status", "DOWN");
                details.put("message", "Database query returned unexpected result");
                return Health.down()
                    .withDetails(details)
                    .build();
            }
            
        } catch (Exception e) {
            details.put("status", "DOWN");
            details.put("error", e.getMessage());
            details.put("error_type", e.getClass().getSimpleName());
            details.put("message", "Database connection failed");
            
            return Health.down()
                .withDetails(details)
                .build();
        }
    }
    
    private void addDatabaseStats(Map<String, Object> details) {
        try {
            // Get database version
            String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
            details.put("database_version", version != null ? version.split(" ")[0] : "Unknown");
            
            // Get active connections count
            Integer activeConnections = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pg_stat_activity WHERE state = 'active'", 
                Integer.class
            );
            details.put("active_connections", activeConnections != null ? activeConnections : 0);
            
            // Get database size
            String dbSize = jdbcTemplate.queryForObject(
                "SELECT pg_size_pretty(pg_database_size(current_database()))", 
                String.class
            );
            details.put("database_size", dbSize != null ? dbSize : "Unknown");
            
            // Get table count
            Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public'", 
                Integer.class
            );
            details.put("table_count", tableCount != null ? tableCount : 0);
            
        } catch (Exception e) {
            // Don't fail the health check if stats collection fails
            details.put("stats_error", "Could not collect database statistics: " + e.getMessage());
        }
    }
} 