package com.todoapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.todoapp.entity.User;
import com.todoapp.repository.NotificationRepository;
import com.todoapp.repository.UserRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import java.util.Optional;
import com.todoapp.dto.NotificationDTO;
import java.util.Arrays;
import java.util.List;
import com.todoapp.entity.Notification;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void getNotifications_ShouldReturnNotifications() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Arrays.asList(new Notification()));
        List<NotificationDTO> notifications = notificationService.getNotifications("test@example.com");
        assertThat(notifications).hasSize(1);
        // Add tests for other methods
    }
} 