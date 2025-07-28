package com.todoapp.controller;

import com.todoapp.dto.TagDTO;
import com.todoapp.dto.CreateTagRequest;
import com.todoapp.dto.UpdateTagRequest;
import com.todoapp.service.TagService;
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
 * Controller for Tag operations.
 */
@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
@Tag(name = "Tags", description = "Tag management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Operation(
        summary = "Get all tags",
        description = "Retrieves all tags for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tags retrieved successfully",
            content = @Content(schema = @Schema(implementation = TagDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<TagDTO>> getTags(Authentication authentication) {
        String userEmail = authentication.getName();
        List<TagDTO> tags = tagService.getTags(userEmail);
        return ResponseEntity.ok(tags);
    }

    @Operation(
        summary = "Create a new tag",
        description = "Creates a new tag for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tag created successfully",
            content = @Content(schema = @Schema(implementation = TagDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<TagDTO> createTag(
            @Parameter(description = "Tag creation details", required = true)
            @Valid @RequestBody CreateTagRequest request,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        TagDTO tag = tagService.createTag(request, userEmail);
        return ResponseEntity.ok(tag);
    }

    @Operation(
        summary = "Update tag",
        description = "Updates an existing tag"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tag updated successfully",
            content = @Content(schema = @Schema(implementation = TagDTO.class))),
        @ApiResponse(responseCode = "404", description = "Tag not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> updateTag(
            @Parameter(description = "Tag ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Tag update details", required = true)
            @Valid @RequestBody UpdateTagRequest request,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        TagDTO tag = tagService.updateTag(id, request, userEmail);
        return ResponseEntity.ok(tag);
    }

    @Operation(
        summary = "Delete tag",
        description = "Deletes a tag by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tag deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Tag not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTag(
            @Parameter(description = "Tag ID", required = true)
            @PathVariable Long id,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        tagService.deleteTag(id, userEmail);
        return ResponseEntity.ok(Map.of("message", "Tag deleted successfully"));
    }
} 