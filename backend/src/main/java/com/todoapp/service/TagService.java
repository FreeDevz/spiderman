package com.todoapp.service;

import com.todoapp.dto.TagDTO;
import com.todoapp.dto.CreateTagRequest;
import com.todoapp.dto.UpdateTagRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for tag operations.
 */
public interface TagService {
    
    /**
     * Get all tags for a user.
     * @param userEmail User email
     * @return List of tags
     */
    List<TagDTO> getTags(String userEmail);
    
    /**
     * Get all tags for a user (without pagination).
     * @param userEmail User email
     * @return List of tags
     */
    List<TagDTO> getAllTags(String userEmail);
    
    /**
     * Create a new tag.
     * @param request Tag creation request
     * @param userEmail User email
     * @return Created tag
     */
    TagDTO createTag(CreateTagRequest request, String userEmail);
    
    /**
     * Update a tag.
     * @param id Tag ID
     * @param request Tag update request
     * @param userEmail User email
     * @return Updated tag
     */
    TagDTO updateTag(Long id, UpdateTagRequest request, String userEmail);
    
    /**
     * Delete a tag.
     * @param id Tag ID
     * @param userEmail User email
     */
    void deleteTag(Long id, String userEmail);
    
    /**
     * Get a specific tag.
     * @param id Tag ID
     * @param userEmail User email
     * @return Tag details
     */
    TagDTO getTag(Long id, String userEmail);
} 