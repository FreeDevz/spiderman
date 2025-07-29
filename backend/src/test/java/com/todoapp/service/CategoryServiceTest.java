package com.todoapp.service;

import com.todoapp.dto.CategoryDTO;
import com.todoapp.dto.CreateCategoryRequest;
import com.todoapp.dto.UpdateCategoryRequest;
import com.todoapp.entity.Category;
import com.todoapp.entity.User;
import com.todoapp.exception.ResourceNotFoundException;
import com.todoapp.repository.CategoryRepository;
import com.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private User testUser;
    private Category workCategory;
    private Category personalCategory;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        workCategory = new Category();
        workCategory.setId(1L);
        workCategory.setName("Work");
        workCategory.setColor("#3B82F6");
        workCategory.setUser(testUser);

        personalCategory = new Category();
        personalCategory.setId(2L);
        personalCategory.setName("Personal");
        personalCategory.setColor("#10B981");
        personalCategory.setUser(testUser);

        createRequest = new CreateCategoryRequest();
        createRequest.setName("New Category");
        createRequest.setColor("#F59E0B");

        updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Updated Category");
        updateRequest.setColor("#EF4444");
    }

    @Test
    void getCategories_WithValidUser_ShouldReturnCategories() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByUserIdOrderByNameAsc(1L)).thenReturn(Arrays.asList(workCategory, personalCategory));

        // When
        List<CategoryDTO> categories = categoryService.getCategories("test@example.com");

        // Then
        assertThat(categories).isNotNull();
        assertThat(categories).hasSize(2);
        assertThat(categories.get(0).getName()).isEqualTo("Work");
        assertThat(categories.get(1).getName()).isEqualTo("Personal");

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).findByUserIdOrderByNameAsc(1L);
    }

    @Test
    void getCategories_WithNoCategories_ShouldReturnEmptyList() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByUserIdOrderByNameAsc(1L)).thenReturn(Arrays.asList());

        // When
        List<CategoryDTO> categories = categoryService.getCategories("test@example.com");

        // Then
        assertThat(categories).isNotNull();
        assertThat(categories).isEmpty();

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).findByUserIdOrderByNameAsc(1L);
    }

    @Test
    void getCategories_WithInvalidUser_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.getCategories("nonexistent@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void getAllCategories_WithValidUser_ShouldReturnAllCategories() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByUserIdOrderByNameAsc(1L)).thenReturn(Arrays.asList(workCategory, personalCategory));

        // When
        List<CategoryDTO> categories = categoryService.getAllCategories("test@example.com");

        // Then
        assertThat(categories).isNotNull();
        assertThat(categories).hasSize(2);

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).findByUserIdOrderByNameAsc(1L);
    }

    @Test
    void createCategory_WithValidRequest_ShouldReturnCreatedCategory() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(3L);
            return category;
        });

        // When
        CategoryDTO createdCategory = categoryService.createCategory(createRequest, "test@example.com");

        // Then
        assertThat(createdCategory).isNotNull();
        assertThat(createdCategory.getName()).isEqualTo("New Category");
        assertThat(createdCategory.getColor()).isEqualTo("#F59E0B");

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_WithInvalidUser_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(createRequest, "nonexistent@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void updateCategory_WithValidRequest_ShouldReturnUpdatedCategory() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(workCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(workCategory);

        // When
        CategoryDTO updatedCategory = categoryService.updateCategory(1L, updateRequest, "test@example.com");

        // Then
        assertThat(updatedCategory).isNotNull();
        assertThat(updatedCategory.getName()).isEqualTo("Updated Category");
        assertThat(updatedCategory.getColor()).isEqualTo("#EF4444");

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).findByIdAndUserId(1L, 1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_WithInvalidCategoryId_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.updateCategory(999L, updateRequest, "test@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category not found");

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).findByIdAndUserId(999L, 1L);
    }

    @Test
    void updateCategory_WithInvalidUser_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.updateCategory(1L, updateRequest, "nonexistent@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void deleteCategory_WithValidId_ShouldDeleteCategory() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(workCategory));

        // When
        categoryService.deleteCategory(1L, "test@example.com");

        // Then
        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).findByIdAndUserId(1L, 1L);
        verify(categoryRepository).delete(workCategory);
    }

    @Test
    void deleteCategory_WithInvalidId_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.deleteCategory(999L, "test@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category not found");

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).findByIdAndUserId(999L, 1L);
    }

    @Test
    void deleteCategory_WithInvalidUser_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.deleteCategory(1L, "nonexistent@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void getCategory_WithValidId_ShouldReturnCategory() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(workCategory));

        // When
        CategoryDTO category = categoryService.getCategory(1L, "test@example.com");

        // Then
        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo("Work");
        assertThat(category.getColor()).isEqualTo("#3B82F6");

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    void getCategory_WithInvalidId_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.getCategory(999L, "test@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category not found");

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).findByIdAndUserId(999L, 1L);
    }

    @Test
    void getCategory_WithInvalidUser_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.getCategory(1L, "nonexistent@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void createCategory_WithNullName_ShouldCreateCategory() {
        // Given
        createRequest.setName(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(3L);
            return category;
        });

        // When
        CategoryDTO createdCategory = categoryService.createCategory(createRequest, "test@example.com");

        // Then
        assertThat(createdCategory).isNotNull();
        assertThat(createdCategory.getName()).isNull();

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_WithEmptyName_ShouldCreateCategory() {
        // Given
        createRequest.setName("");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(3L);
            return category;
        });

        // When
        CategoryDTO createdCategory = categoryService.createCategory(createRequest, "test@example.com");

        // Then
        assertThat(createdCategory).isNotNull();
        assertThat(createdCategory.getName()).isEqualTo("");

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_WithNullName_ShouldUpdateCategory() {
        // Given
        updateRequest.setName(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(workCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(workCategory);

        // When
        CategoryDTO updatedCategory = categoryService.updateCategory(1L, updateRequest, "test@example.com");

        // Then
        assertThat(updatedCategory).isNotNull();
        assertThat(updatedCategory.getName()).isEqualTo("Work"); // Original name unchanged

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).findByIdAndUserId(1L, 1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void categoryDTO_Mapping_ShouldBeCorrect() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByUserIdOrderByNameAsc(1L)).thenReturn(Arrays.asList(workCategory));

        // When
        List<CategoryDTO> categories = categoryService.getCategories("test@example.com");

        // Then
        assertThat(categories).hasSize(1);
        CategoryDTO categoryDTO = categories.get(0);
        assertThat(categoryDTO.getId()).isEqualTo(1L);
        assertThat(categoryDTO.getName()).isEqualTo("Work");
        assertThat(categoryDTO.getColor()).isEqualTo("#3B82F6");

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).findByUserIdOrderByNameAsc(1L);
    }

    @Test
    void createCategory_WithDuplicateName_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.existsByNameAndUserId("New Category", 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(createRequest, "test@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category with this name already exists");

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).existsByNameAndUserId("New Category", 1L);
    }

    @Test
    void updateCategory_WithDuplicateName_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(workCategory));
        when(categoryRepository.existsByNameAndUserId("Updated Category", 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.updateCategory(1L, updateRequest, "test@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category with this name already exists");

        verify(userRepository).findByEmail("test@example.com");
        verify(categoryRepository).findByIdAndUserId(1L, 1L);
        verify(categoryRepository).existsByNameAndUserId("Updated Category", 1L);
    }
} 