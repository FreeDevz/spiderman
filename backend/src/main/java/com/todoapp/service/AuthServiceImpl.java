package com.todoapp.service;

import com.todoapp.dto.AuthRequest;
import com.todoapp.dto.AuthResponse;
import com.todoapp.dto.RegisterRequest;
import com.todoapp.dto.UserDTO;
import com.todoapp.entity.User;
import com.todoapp.repository.UserRepository;
import com.todoapp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of AuthService.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, 
                          PasswordEncoder passwordEncoder, 
                          JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        // Generate tokens
        String token = jwtUtil.generateToken(savedUser.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail());

        return new AuthResponse(token, refreshToken, convertToDTO(savedUser), jwtUtil.getExpirationInMs());
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Get user from database
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate tokens
        String token = jwtUtil.generateToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return new AuthResponse(token, refreshToken, convertToDTO(user), jwtUtil.getExpirationInMs());
    }

    @Override
    public void logout() {
        // In a stateless JWT implementation, logout is handled client-side
        // by removing the token. Server-side logout would require a token blacklist.
        // For now, we'll just return success.
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        // Remove "Bearer " prefix if present
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        // Validate refresh token
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Get user email from token
        String email = jwtUtil.getEmailFromRefreshToken(refreshToken);
        
        // Get user from database
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate new tokens
        String newToken = jwtUtil.generateToken(user.getEmail());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return new AuthResponse(newToken, newRefreshToken, convertToDTO(user), jwtUtil.getExpirationInMs());
    }

    @Override
    public void forgotPassword(String email) {
        // Check if user exists
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Don't reveal if user exists or not for security
            return;
        }

        // Generate reset token and send email
        // This is a placeholder implementation
        // In a real application, you would:
        // 1. Generate a reset token
        // 2. Store it in the database with expiration
        // 3. Send an email with the reset link
        // 4. Use a proper email service
        
        // For now, we'll just log the action
        System.out.println("Password reset requested for email: " + email);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        // Validate reset token and update password
        // This is a placeholder implementation
        // In a real application, you would:
        // 1. Validate the reset token
        // 2. Check if it's expired
        // 3. Update the user's password
        // 4. Invalidate the reset token
        
        // For now, we'll just log the action
        System.out.println("Password reset with token: " + token);
    }

    @Override
    public void verifyEmail(String token) {
        // Verify email using token
        // This is a placeholder implementation
        // In a real application, you would:
        // 1. Validate the verification token
        // 2. Update the user's email_verified field
        // 3. Invalidate the verification token
        
        // For now, we'll just log the action
        System.out.println("Email verification with token: " + token);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
} 