package com.todoapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.name:TodoApp}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(servers())
                .components(components())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .addSecurityItem(new SecurityRequirement().addList("Basic Authentication"));
    }

    private Info apiInfo() {
        return new Info()
                .title(appName + " API")
                .description("""
                        A comprehensive REST API for managing tasks, categories, tags, and user preferences.
                        
                        ## Features
                        - **Authentication & Authorization**: JWT-based authentication with refresh tokens
                        - **Task Management**: Create, read, update, delete, and bulk operations on tasks
                        - **Category Management**: Organize tasks with categories
                        - **Tag Management**: Add tags to tasks for better organization
                        - **User Management**: Profile management and settings
                        - **Dashboard**: Statistics and analytics
                        - **Notifications**: Real-time notifications and settings
                        
                        ## Authentication
                        Most endpoints require authentication. Use the `/api/auth/login` endpoint to obtain a JWT token,
                        then include it in the Authorization header as `Bearer <token>`.
                        
                        ## Rate Limiting
                        API requests are rate-limited to ensure fair usage.
                        
                        ## Error Handling
                        The API returns consistent error responses with appropriate HTTP status codes.
                        """)
                .version(appVersion)
                .contact(new Contact()
                        .name("TodoApp Team")
                        .email("support@todoapp.com")
                        .url("https://todoapp.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> servers() {
        return List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Local Development Server"),
                new Server()
                        .url("https://api.todoapp.com")
                        .description("Production Server"),
                new Server()
                        .url("https://staging-api.todoapp.com")
                        .description("Staging Server")
        );
    }

    private Components components() {
        return new Components()
                .addSecuritySchemes("Bearer Authentication", bearerAuth())
                .addSecuritySchemes("Basic Authentication", basicAuth());
    }

    private SecurityScheme bearerAuth() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token obtained from /api/auth/login endpoint");
    }

    private SecurityScheme basicAuth() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic")
                .description("Basic authentication for development purposes");
    }
} 