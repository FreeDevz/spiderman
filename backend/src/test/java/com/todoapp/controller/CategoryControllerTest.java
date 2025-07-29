package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.dto.CategoryDTO;
import com.todoapp.dto.CreateCategoryRequest;
import com.todoapp.dto.UpdateCategoryRequest;
import com.todoapp.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getCategories_ShouldReturnCategoriesList() throws Exception {
        // Given
        String userEmail = "test@example.com";
        List<CategoryDTO> categories = Arrays.asList(
            createSampleCategoryDTO(1L, "Work", "#FF5733"),
            createSampleCategoryDTO(2L, "Personal", "#33FF57")
        );
        
        when(authentication.getName()).thenReturn(userEmail);
        when(categoryService.getCategories(userEmail)).thenReturn(categories);

        // When & Then
        mockMvc.perform(get("/api/categories")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Work"))
                .andExpect(jsonPath("$[0].color").value("#FF5733"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Personal"))
                .andExpect(jsonPath("$[1].color").value("#33FF57"));
    }

    @Test
    void createCategory_ShouldReturnCreatedCategory() throws Exception {
        // Given
        String userEmail = "test@example.com";
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("New Category");
        request.setColor("#FF0000");
        request.setDescription("A new category");
        
        CategoryDTO createdCategory = createSampleCategoryDTO(1L, "New Category", "#FF0000");
        createdCategory.setDescription("A new category");
        
        when(authentication.getName()).thenReturn(userEmail);
        when(categoryService.createCategory(any(CreateCategoryRequest.class), anyString())).thenReturn(createdCategory);

        // When & Then
        mockMvc.perform(post("/api/categories")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Category"))
                .andExpect(jsonPath("$.color").value("#FF0000"))
                .andExpect(jsonPath("$.description").value("A new category"));
    }

    @Test
    void createCategory_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateCategoryRequest request = new CreateCategoryRequest();
        // Empty request will trigger validation errors
        
        when(authentication.getName()).thenReturn("test@example.com");

        // When & Then
        mockMvc.perform(post("/api/categories")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() throws Exception {
        // Given
        String userEmail = "test@example.com";
        Long categoryId = 1L;
        
        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("Updated Category");
        request.setColor("#00FF00");
        request.setDescription("Updated description");
        
        CategoryDTO updatedCategory = createSampleCategoryDTO(categoryId, "Updated Category", "#00FF00");
        updatedCategory.setDescription("Updated description");
        
        when(authentication.getName()).thenReturn(userEmail);
        when(categoryService.updateCategory(anyLong(), any(UpdateCategoryRequest.class), anyString())).thenReturn(updatedCategory);

        // When & Then
        mockMvc.perform(put("/api/categories/{id}", categoryId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Updated Category"))
                .andExpect(jsonPath("$.color").value("#00FF00"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void updateCategory_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        Long categoryId = 1L;
        UpdateCategoryRequest request = new UpdateCategoryRequest();
        // Empty request will trigger validation errors
        
        when(authentication.getName()).thenReturn("test@example.com");

        // When & Then
        mockMvc.perform(put("/api/categories/{id}", categoryId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCategory_ShouldReturnSuccessMessage() throws Exception {
        // Given
        String userEmail = "test@example.com";
        Long categoryId = 1L;
        
        when(authentication.getName()).thenReturn(userEmail);

        // When & Then
        mockMvc.perform(delete("/api/categories/{id}", categoryId)
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted successfully"));
    }

    private CategoryDTO createSampleCategoryDTO(Long id, String name, String color) {
        CategoryDTO category = new CategoryDTO();
        category.setId(id);
        category.setName(name);
        category.setColor(color);
        category.setDescription("Sample category description");
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }
} 