package com.todoapp.controller;

import com.todoapp.dto.CategoryDTO;
import com.todoapp.dto.CreateCategoryRequest;
import com.todoapp.dto.UpdateCategoryRequest;
import com.todoapp.service.CategoryService;
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

import java.util.List;
import java.util.Map;

/**
 * Controller for Category operations.
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
@Tag(name = "Categories", description = "Category management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(
        summary = "Get all categories",
        description = "Retrieves all categories for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully",
            content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategories(Authentication authentication) {
        String userEmail = authentication.getName();
        List<CategoryDTO> categories = categoryService.getCategories(userEmail);
        return ResponseEntity.ok(categories);
    }

    @Operation(
        summary = "Create a new category",
        description = "Creates a new category for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category created successfully",
            content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(
            @Parameter(description = "Category creation details", required = true)
            @Valid @RequestBody CreateCategoryRequest request,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        CategoryDTO category = categoryService.createCategory(request, userEmail);
        return ResponseEntity.ok(category);
    }

    @Operation(
        summary = "Update category",
        description = "Updates an existing category"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category updated successfully",
            content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Category update details", required = true)
            @Valid @RequestBody UpdateCategoryRequest request,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        CategoryDTO category = categoryService.updateCategory(id, request, userEmail);
        return ResponseEntity.ok(category);
    }

    @Operation(
        summary = "Delete category",
        description = "Deletes a category by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long id,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        categoryService.deleteCategory(id, userEmail);
        return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
    }
} 