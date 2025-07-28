package com.todoapp.controller;

import com.todoapp.dto.NotificationDTO;
import com.todoapp.dto.NotificationSettingsDTO;
import com.todoapp.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for Notification operations.
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
@Tag(name = "Notifications", description = "Notification management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(
        summary = "Get notifications",
        description = "Retrieves all notifications for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully",
            content = @Content(schema = @Schema(implementation = NotificationDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications(Authentication authentication) {
        String userEmail = authentication.getName();
        List<NotificationDTO> notifications = notificationService.getNotifications(userEmail);
        return ResponseEntity.ok(notifications);
    }

    @Operation(
        summary = "Mark notification as read",
        description = "Marks a specific notification as read"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification marked as read"),
        @ApiResponse(responseCode = "404", description = "Notification not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(
            @Parameter(description = "Notification ID", required = true)
            @PathVariable Long id,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        notificationService.markAsRead(id, userEmail);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
    }

    @Operation(
        summary = "Update notification settings",
        description = "Updates the user's notification preferences"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Settings updated successfully",
            content = @Content(schema = @Schema(implementation = NotificationSettingsDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/settings")
    public ResponseEntity<NotificationSettingsDTO> updateSettings(
            @Parameter(description = "Notification settings", required = true)
            @Valid @RequestBody NotificationSettingsDTO request,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        NotificationSettingsDTO settings = notificationService.updateSettings(request, userEmail);
        return ResponseEntity.ok(settings);
    }
} 