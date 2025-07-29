package com.todoapp.controller;

import com.todoapp.dto.AuthRequest;
import com.todoapp.dto.AuthResponse;
import com.todoapp.dto.RegisterRequest;
import com.todoapp.dto.UserDTO;
import com.todoapp.exception.BusinessException;
import com.todoapp.exception.ResourceNotFoundException;
import com.todoapp.exception.TokenExpiredException;
import com.todoapp.service.AuthService;
import com.todoapp.service.UserServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for Authentication operations.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;
    private final UserServiceInterface userService;

    @Autowired
    public AuthController(AuthService authService, UserServiceInterface userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Parameter(description = "User registration details", required = true)
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Authenticate user",
        description = "Authenticates a user with email and password, returns JWT tokens"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Login credentials", required = true)
            @Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Logout user",
        description = "Logs out the current user and invalidates the session"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        authService.logout();
        return ResponseEntity.ok(Map.of("message", "Successfully logged out"));
    }

    @Operation(
        summary = "Refresh access token",
        description = "Refreshes the access token using a valid refresh token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token"),
        @ApiResponse(responseCode = "400", description = "Invalid token format")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @Parameter(description = "Refresh token from Authorization header", required = true)
            @RequestHeader("Authorization") String refreshToken) {
        try {
            // Remove "Bearer " prefix if present
            String token = refreshToken.startsWith("Bearer ") ? refreshToken.substring(7) : refreshToken;
            AuthResponse response = authService.refreshToken(token);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid token format");
        }
    }

    @Operation(
        summary = "Request password reset",
        description = "Sends a password reset email to the specified email address"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset email sent"),
        @ApiResponse(responseCode = "400", description = "Invalid email format"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Parameter(description = "Email address for password reset", required = true)
            @RequestBody Map<String, String> request) {
        try {
            if (request == null) {
                throw new BusinessException("Request body is required");
            }
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                throw new BusinessException("Email is required");
            }
            authService.forgotPassword(email);
            return ResponseEntity.ok(Map.of("message", "Password reset email sent"));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid email format");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                throw new ResourceNotFoundException("User not found");
            }
            throw e;
        }
    }

    @Operation(
        summary = "Reset password",
        description = "Resets the user password using a valid reset token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successful"),
        @ApiResponse(responseCode = "400", description = "Invalid token or password"),
        @ApiResponse(responseCode = "401", description = "Token expired or invalid")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Parameter(description = "Password reset request with token and new password", required = true)
            @RequestBody Map<String, String> request) {
        try {
            if (request == null) {
                throw new BusinessException("Request body is required");
            }
            String token = request.get("token");
            String newPassword = request.get("newPassword");
            
            if (token == null || token.trim().isEmpty()) {
                throw new BusinessException("Token is required");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                throw new BusinessException("New password is required");
            }
            
            authService.resetPassword(token, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password reset successful"));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid token or password");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("expired")) {
                throw new BusinessException("Token expired");
            }
            throw e;
        }
    }

    @Operation(
        summary = "Verify email address",
        description = "Verifies the user's email address using a verification token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid token"),
        @ApiResponse(responseCode = "401", description = "Token expired or invalid")
    })
    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @Parameter(description = "Email verification token", required = true)
            @RequestParam String token) {
        try {
            authService.verifyEmail(token);
            return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid verification token");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("expired")) {
                throw new TokenExpiredException("Token expired");
            }
            throw e;
        }
    }
} 