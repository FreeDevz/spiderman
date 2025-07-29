package com.todoapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HealthControllerTest {

    @Mock
    private HealthIndicator databaseHealthIndicator;

    @InjectMocks
    private HealthController healthController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(healthController).build();
    }

    @Test
    void health_ShouldReturnApplicationHealth() throws Exception {
        // When & Then
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.environment").value("development"))
                .andExpect(jsonPath("$.message").value("Application is running"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void databaseHealth_WhenDatabaseIsHealthy_ShouldReturn200() throws Exception {
        // Given
        Health health = Health.up()
                .withDetail("database", "PostgreSQL")
                .withDetail("status", "connected")
                .build();
        
        when(databaseHealthIndicator.health()).thenReturn(health);

        // When & Then
        mockMvc.perform(get("/health/database"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.database").value("UP"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.details.database").value("PostgreSQL"))
                .andExpect(jsonPath("$.details.status").value("connected"));
    }

    @Test
    void databaseHealth_WhenDatabaseIsUnhealthy_ShouldReturn503() throws Exception {
        // Given
        Health health = Health.down()
                .withDetail("database", "PostgreSQL")
                .withDetail("error", "Connection failed")
                .build();
        
        when(databaseHealthIndicator.health()).thenReturn(health);

        // When & Then
        mockMvc.perform(get("/health/database"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.database").value("DOWN"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.details.database").value("PostgreSQL"))
                .andExpect(jsonPath("$.details.error").value("Connection failed"));
    }

    @Test
    void databaseHealth_WhenDatabaseIsUnknown_ShouldReturn503() throws Exception {
        // Given
        Health health = Health.unknown()
                .withDetail("database", "PostgreSQL")
                .withDetail("status", "unknown")
                .build();
        
        when(databaseHealthIndicator.health()).thenReturn(health);

        // When & Then
        mockMvc.perform(get("/health/database"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.database").value("UNKNOWN"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.details.database").value("PostgreSQL"))
                .andExpect(jsonPath("$.details.status").value("unknown"));
    }
} 