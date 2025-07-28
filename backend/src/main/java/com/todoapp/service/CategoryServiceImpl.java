package com.todoapp.service;

import com.todoapp.dto.CategoryDTO;
import com.todoapp.dto.CreateCategoryRequest;
import com.todoapp.dto.UpdateCategoryRequest;
import com.todoapp.entity.Category;
import com.todoapp.entity.User;
import com.todoapp.repository.CategoryRepository;
import com.todoapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for category operations.
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CategoryDTO> getCategories(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<Category> categories = categoryRepository.findByUserIdOrderByNameAsc(user.getId());
        return categories.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getAllCategories(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<Category> categories = categoryRepository.findByUserIdOrderByNameAsc(user.getId());
        return categories.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO createCategory(CreateCategoryRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);
        
        // Check if category with same name already exists
        if (categoryRepository.existsByNameAndUserId(request.getName(), user.getId())) {
            throw new RuntimeException("Category with this name already exists");
        }
        
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setColor(request.getColor());
        category.setUser(user);
        
        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    @Override
    public CategoryDTO updateCategory(Long id, UpdateCategoryRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Check if new name conflicts with existing category
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByNameAndUserId(request.getName(), user.getId())) {
                throw new RuntimeException("Category with this name already exists");
            }
            category.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getColor() != null) {
            category.setColor(request.getColor());
        }
        
        Category updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Check if category has tasks
        if (!category.getTasks().isEmpty()) {
            throw new RuntimeException("Cannot delete category with existing tasks");
        }
        
        categoryRepository.delete(category);
    }

    @Override
    public CategoryDTO getCategory(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new RuntimeException("Category not found"));
        return convertToDTO(category);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setColor(category.getColor());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        dto.setTaskCount(category.getTasks() != null ? category.getTasks().size() : 0);
        return dto;
    }
} 