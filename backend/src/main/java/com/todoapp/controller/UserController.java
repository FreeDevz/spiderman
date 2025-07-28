package com.todoapp.controller;

import com.todoapp.dto.UserDTO;
import com.todoapp.dto.UpdateUserRequest;
import com.todoapp.dto.UserSettingsDTO;
import com.todoapp.service.UserService;
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

import java.util.Map;

/**
 * Controller for User operations.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "Users", description = "User management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
        summary = "Get user profile",
        description = "Retrieves the current user's profile information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(Authentication authentication) {
        String userEmail = authentication.getName();
        UserDTO user = userService.getUserByEmail(userEmail);
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "Update user profile",
        description = "Updates the current user's profile information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            @Parameter(description = "Profile update details", required = true)
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        UserDTO user = userService.updateProfile(request, userEmail);
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "Delete user account",
        description = "Deletes the current user's account"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/account")
    public ResponseEntity<Map<String, String>> deleteAccount(Authentication authentication) {
        String userEmail = authentication.getName();
        userService.deleteAccount(userEmail);
        return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
    }

    @Operation(
        summary = "Get user settings",
        description = "Retrieves the current user's settings"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Settings retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserSettingsDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/settings")
    public ResponseEntity<UserSettingsDTO> getSettings(Authentication authentication) {
        String userEmail = authentication.getName();
        UserSettingsDTO settings = userService.getUserSettings(userEmail);
        return ResponseEntity.ok(settings);
    }

    @Operation(
        summary = "Update user settings",
        description = "Updates the current user's settings"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Settings updated successfully",
            content = @Content(schema = @Schema(implementation = UserSettingsDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/settings")
    public ResponseEntity<UserSettingsDTO> updateSettings(
            @Parameter(description = "Settings update details", required = true)
            @Valid @RequestBody UserSettingsDTO request,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        UserSettingsDTO settings = userService.updateUserSettings(request, userEmail);
        return ResponseEntity.ok(settings);
    }

    // Legacy endpoints for testing
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getUserCount() {
        long count = userService.countUsers();
        return ResponseEntity.ok(Map.of(
            "userCount", count,
            "message", "Database connection successful"
        ));
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        try {
            long count = userService.countUsers();
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "userCount", count,
                "message", "Database connection and query successful"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "status", "ERROR",
                "message", "Database connection failed: " + e.getMessage()
            ));
        }
    }
} 