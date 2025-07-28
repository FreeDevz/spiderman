package com.todoapp.service;

import com.todoapp.dto.UserDTO;
import com.todoapp.dto.UserSettingsDTO;
import com.todoapp.dto.UpdateUserRequest;
import com.todoapp.entity.User;
import com.todoapp.entity.UserSettings;
import com.todoapp.repository.UserRepository;
import com.todoapp.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for User operations.
 */
@Service
@Transactional
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserSettingsRepository userSettingsRepository) {
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDTO getUserProfile(String userEmail) {
        User user = getUserByEmailInternal(userEmail);
        return convertToDTO(user);
    }

    @Override
    public UserDTO updateUserProfile(UpdateUserRequest request, String userEmail) {
        User user = getUserByEmailInternal(userEmail);
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // Check if new email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    public void deleteUserAccount(String userEmail) {
        User user = getUserByEmailInternal(userEmail);
        userRepository.delete(user);
    }

    @Override
    public UserSettingsDTO getUserSettings(String userEmail) {
        User user = getUserByEmailInternal(userEmail);
        UserSettings settings = userSettingsRepository.findByUserId(user.getId())
            .orElseGet(() -> createDefaultUserSettings(user));
        
        return convertToUserSettingsDTO(settings);
    }

    @Override
    public UserSettingsDTO updateUserSettings(UserSettingsDTO settingsDTO, String userEmail) {
        User user = getUserByEmailInternal(userEmail);
        UserSettings settings = userSettingsRepository.findByUserId(user.getId())
            .orElseGet(() -> createDefaultUserSettings(user));
        
        // Update settings
        if (settingsDTO.getTheme() != null) {
            try {
                settings.setTheme(UserSettings.Theme.valueOf(settingsDTO.getTheme().toUpperCase()));
            } catch (IllegalArgumentException e) {
                settings.setTheme(UserSettings.Theme.LIGHT);
            }
        }
        if (settingsDTO.getLanguage() != null) {
            settings.setLanguage(settingsDTO.getLanguage());
        }
        if (settingsDTO.getTimeZone() != null) {
            settings.setTimeZone(settingsDTO.getTimeZone());
        }
        if (settingsDTO.getEmailNotifications() != null) {
            settings.setEmailNotifications(settingsDTO.getEmailNotifications());
        }
        if (settingsDTO.getPushNotifications() != null) {
            settings.setPushNotifications(settingsDTO.getPushNotifications());
        }
        if (settingsDTO.getTaskReminders() != null) {
            settings.setTaskReminders(settingsDTO.getTaskReminders());
        }
        if (settingsDTO.getDailyDigest() != null) {
            settings.setDailyDigest(settingsDTO.getDailyDigest());
        }
        if (settingsDTO.getWeeklyReport() != null) {
            settings.setWeeklyReport(settingsDTO.getWeeklyReport());
        }
        
        UserSettings savedSettings = userSettingsRepository.save(settings);
        return convertToUserSettingsDTO(savedSettings);
    }

    @Override
    public UserDTO getUserByEmail(String userEmail) {
        User user = getUserByEmailInternal(userEmail);
        return convertToDTO(user);
    }

    @Override
    public UserDTO updateProfile(UpdateUserRequest request, String userEmail) {
        return updateUserProfile(request, userEmail);
    }

    @Override
    public void deleteAccount(String userEmail) {
        deleteUserAccount(userEmail);
    }

    private User getUserByEmailInternal(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserSettings createDefaultUserSettings(User user) {
        UserSettings settings = new UserSettings();
        settings.setUser(user);
        settings.setTheme(UserSettings.Theme.LIGHT);
        settings.setLanguage("en");
        settings.setTimeZone("UTC");
        settings.setEmailNotifications(true);
        settings.setPushNotifications(true);
        settings.setTaskReminders(true);
        settings.setDailyDigest(false);
        settings.setWeeklyReport(true);
        return userSettingsRepository.save(settings);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setActive(user.getActive());
        return dto;
    }

    private UserSettingsDTO convertToUserSettingsDTO(UserSettings settings) {
        UserSettingsDTO dto = new UserSettingsDTO();
        dto.setTheme(settings.getTheme().name().toLowerCase());
        dto.setLanguage(settings.getLanguage());
        dto.setTimeZone(settings.getTimeZone());
        dto.setEmailNotifications(settings.getEmailNotifications());
        dto.setPushNotifications(settings.getPushNotifications());
        dto.setTaskReminders(settings.getTaskReminders());
        dto.setDailyDigest(settings.getDailyDigest());
        dto.setWeeklyReport(settings.getWeeklyReport());
        return dto;
    }
} 