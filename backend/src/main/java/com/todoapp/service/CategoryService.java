package com.todoapp.service;

import com.todoapp.dto.CategoryDTO;
import com.todoapp.dto.CreateCategoryRequest;
import com.todoapp.dto.UpdateCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for category operations.
 */
public interface CategoryService {
    
    /**
     * Get all categories for a user.
     * @param userEmail User email
     * @return List of categories
     */
    List<CategoryDTO> getCategories(String userEmail);
    
    /**
     * Get all categories for a user (without pagination).
     * @param userEmail User email
     * @return List of categories
     */
    List<CategoryDTO> getAllCategories(String userEmail);
    
    /**
     * Create a new category.
     * @param request Category creation request
     * @param userEmail User email
     * @return Created category
     */
    CategoryDTO createCategory(CreateCategoryRequest request, String userEmail);
    
    /**
     * Update a category.
     * @param id Category ID
     * @param request Category update request
     * @param userEmail User email
     * @return Updated category
     */
    CategoryDTO updateCategory(Long id, UpdateCategoryRequest request, String userEmail);
    
    /**
     * Delete a category.
     * @param id Category ID
     * @param userEmail User email
     */
    void deleteCategory(Long id, String userEmail);
    
    /**
     * Get a specific category.
     * @param id Category ID
     * @param userEmail User email
     * @return Category details
     */
    CategoryDTO getCategory(Long id, String userEmail);
} 