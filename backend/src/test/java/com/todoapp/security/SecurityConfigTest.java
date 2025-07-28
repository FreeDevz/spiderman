package com.todoapp.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic test to verify security configuration loads correctly.
 */
@SpringBootTest
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Test
    public void contextLoads() {
        // This test verifies that the Spring context loads successfully
        // with all security components properly configured
    }
} 