package com.todoapp.service;

import com.todoapp.dto.BulkTaskRequest;
import com.todoapp.dto.CreateTaskRequest;
import com.todoapp.dto.TaskDTO;
import com.todoapp.dto.UpdateTaskRequest;
import com.todoapp.entity.Category;
import com.todoapp.entity.Tag;
import com.todoapp.entity.Task;
import com.todoapp.entity.User;
import com.todoapp.exception.ResourceNotFoundException;
import com.todoapp.repository.CategoryRepository;
import com.todoapp.repository.TagRepository;
import com.todoapp.repository.TaskRepository;
import com.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User testUser;
    private Category testCategory;
    private Tag testTag;
    private Task testTask;
    private CreateTaskRequest createRequest;
    private UpdateTaskRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Work");
        testCategory.setColor("#3B82F6");
        testCategory.setUser(testUser);

        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("urgent");
        testTag.setUser(testUser);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(Task.TaskStatus.PENDING);
        testTask.setPriority(Task.TaskPriority.MEDIUM);
        testTask.setDueDate(LocalDateTime.now().plusDays(1));
        testTask.setUser(testUser);
        testTask.setCategory(testCategory);
        testTask.setTags(new HashSet<>(Arrays.asList(testTag)));

        createRequest = new CreateTaskRequest();
        createRequest.setTitle("New Task");
        createRequest.setDescription("New Description");
        createRequest.setPriority("HIGH");
        createRequest.setCategoryId(1L);
        createRequest.setTagIds(Arrays.asList(1L));
        createRequest.setDueDate(LocalDateTime.now().plusDays(1));

        updateRequest = new UpdateTaskRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPriority("LOW");
        updateRequest.setCategoryId(1L);
        updateRequest.setTagIds(Arrays.asList(1L));
    }

    @Test
    void createTask_WithValidRequest_ShouldReturnTaskDTO() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(tagRepository.findAllById(Arrays.asList(1L))).thenReturn(Arrays.asList(testTag));
        Task savedTask = new Task();
        savedTask.setTitle("New Task");
        savedTask.setDescription("New Description");
        when(taskRepository.save(any())).thenReturn(savedTask);

        // When
        TaskDTO result = taskService.createTask(createRequest, "test@example.com");

        // Then
        assertThat(result.getTitle()).isEqualTo("New Task");
        assertThat(result.getDescription()).isEqualTo("New Description");
        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).findById(1L);
        verify(tagRepository).findAllById(Arrays.asList(1L));
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_WithInvalidUser_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.createTask(createRequest, "nonexistent@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void createTask_WithInvalidCategory_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        createRequest.setCategoryId(999L);

        // When & Then
        assertThatThrownBy(() -> taskService.createTask(createRequest, "test@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category not found");

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).findById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getTasks_WithValidUser_ShouldReturnPageOfTasks() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Task> tasks = Arrays.asList(testTask);
        Page<Task> taskPage = new PageImpl<>(tasks, pageable, 1);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByUserId(1L, pageable)).thenReturn(taskPage);

        // When
        Page<TaskDTO> result = taskService.getTasks("test@example.com", null, null, null, null, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Task");

        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findByUserId(1L, pageable);
    }

    @Test
    void getTasks_WithStatusFilter_ShouldApplyStatusFilter() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Task> tasks = Arrays.asList(testTask);
        Page<Task> taskPage = new PageImpl<>(tasks, pageable, 1);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByUserIdAndStatus(1L, Task.TaskStatus.PENDING, pageable)).thenReturn(taskPage);

        // When
        Page<TaskDTO> result = taskService.getTasks("test@example.com", "PENDING", null, null, null, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findByUserIdAndStatus(1L, Task.TaskStatus.PENDING, pageable);
    }

    @Test
    void getTask_WithValidId_ShouldReturnTaskDTO() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        Task foundTask = new Task();
        foundTask.setTitle("Test Task");
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(foundTask));

        // When
        TaskDTO result = taskService.getTask(1L, "test@example.com");

        // Then
        assertThat(result.getTitle()).isEqualTo("Test Task");
        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    void getTask_WithInvalidId_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.getTask(999L, "test@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found");

        verify(taskRepository).findByIdAndUserId(999L, 1L);
    }

    @Test
    void updateTask_WithValidRequest_ShouldReturnUpdatedTaskDTO() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTask));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(tagRepository.findAllById(Arrays.asList(1L))).thenReturn(Arrays.asList(testTag));
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task");
        when(taskRepository.save(any())).thenReturn(updatedTask);

        // When
        TaskDTO result = taskService.updateTask(1L, updateRequest, "test@example.com");

        // Then
        assertThat(result.getTitle()).isEqualTo("Updated Task");
        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findByIdAndUserId(1L, 1L);
        verify(categoryRepository).findById(1L);
        verify(tagRepository).findAllById(Arrays.asList(1L));
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_WithInvalidTaskId_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.updateTask(999L, updateRequest, "test@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found");

        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findByIdAndUserId(999L, 1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_WithValidId_ShouldDeleteTask() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        Task deleteTask = new Task();
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(deleteTask));
        when(taskRepository.save(any(Task.class))).thenReturn(deleteTask);

        // When
        taskService.deleteTask(1L, "test@example.com");

        // Then
        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findByIdAndUserId(1L, 1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deleteTask_WithInvalidId_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTask(999L, "test@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found");

        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findByIdAndUserId(999L, 1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTaskStatus_WithValidStatus_ShouldUpdateStatus() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTask));
        Task statusTask = new Task();
        statusTask.setStatus(Task.TaskStatus.COMPLETED);
        when(taskRepository.save(any())).thenReturn(statusTask);

        // When
        TaskDTO result = taskService.updateTaskStatus(1L, "COMPLETED", "test@example.com");

        // Then
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findByIdAndUserId(1L, 1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTaskStatus_WithInvalidStatus_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTask));

        // When & Then
        assertThatThrownBy(() -> taskService.updateTaskStatus(1L, "INVALID_STATUS", "test@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No enum constant com.todoapp.entity.Task.TaskStatus.INVALID_STATUS");

        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findByIdAndUserId(1L, 1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void bulkOperations_WithValidRequest_ShouldCompleteSuccessfully() {
        // Given
        BulkTaskRequest bulkRequest = new BulkTaskRequest();
        bulkRequest.setTaskIds(Arrays.asList(1L, 2L));
        bulkRequest.setOperation("DELETE");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(testTask, testTask));
        when(taskRepository.saveAll(anyList())).thenReturn(Arrays.asList(testTask, testTask));

        // When
        Map<String, Object> result = taskService.bulkOperations(bulkRequest, "test@example.com");

        // Then
        assertThat(result).containsKey("deleted");
        assertThat(result.get("deleted")).isEqualTo(2);
        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findAllById(Arrays.asList(1L, 2L));
        verify(taskRepository).saveAll(anyList());
    }

    @Test
    void exportTasks_WithValidFormat_ShouldReturnExportedData() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByUserId(1L, Pageable.unpaged())).thenReturn(new PageImpl<>(Arrays.asList(testTask)));

        // When
        String result = taskService.exportTasks("test@example.com", "json");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("Test Task");
        verify(userRepository).findByEmail("test@example.com");
        verify(taskRepository).findByUserId(1L, Pageable.unpaged());
    }
} 