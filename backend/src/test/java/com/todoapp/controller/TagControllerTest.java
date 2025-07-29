package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.dto.CreateTagRequest;
import com.todoapp.dto.TagDTO;
import com.todoapp.dto.UpdateTagRequest;
import com.todoapp.service.TagService;
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
class TagControllerTest {

    @Mock
    private TagService tagService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TagController tagController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tagController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getTags_ShouldReturnTagsList() throws Exception {
        // Given
        String userEmail = "test@example.com";
        List<TagDTO> tags = Arrays.asList(
            createSampleTagDTO(1L, "urgent", "#FF0000"),
            createSampleTagDTO(2L, "important", "#FFA500"),
            createSampleTagDTO(3L, "personal", "#008000")
        );
        
        when(authentication.getName()).thenReturn(userEmail);
        when(tagService.getTags(userEmail)).thenReturn(tags);

        // When & Then
        mockMvc.perform(get("/api/tags")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("urgent"))
                .andExpect(jsonPath("$[0].color").value("#FF0000"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("important"))
                .andExpect(jsonPath("$[1].color").value("#FFA500"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("personal"))
                .andExpect(jsonPath("$[2].color").value("#008000"));
    }

    @Test
    void createTag_ShouldReturnCreatedTag() throws Exception {
        // Given
        String userEmail = "test@example.com";
        CreateTagRequest request = new CreateTagRequest();
        request.setName("new-tag");
        request.setColor("#FF00FF");
        
        TagDTO createdTag = createSampleTagDTO(1L, "new-tag", "#FF00FF");
        
        when(authentication.getName()).thenReturn(userEmail);
        when(tagService.createTag(any(CreateTagRequest.class), anyString())).thenReturn(createdTag);

        // When & Then
        mockMvc.perform(post("/api/tags")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("new-tag"))
                .andExpect(jsonPath("$.color").value("#FF00FF"));
    }

    @Test
    void createTag_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateTagRequest request = new CreateTagRequest();
        // Empty request will trigger validation errors
        
        when(authentication.getName()).thenReturn("test@example.com");

        // When & Then
        mockMvc.perform(post("/api/tags")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTag_ShouldReturnUpdatedTag() throws Exception {
        // Given
        String userEmail = "test@example.com";
        Long tagId = 1L;
        
        UpdateTagRequest request = new UpdateTagRequest();
        request.setName("updated-tag");
        request.setColor("#00FFFF");
        
        TagDTO updatedTag = createSampleTagDTO(tagId, "updated-tag", "#00FFFF");
        
        when(authentication.getName()).thenReturn(userEmail);
        when(tagService.updateTag(anyLong(), any(UpdateTagRequest.class), anyString())).thenReturn(updatedTag);

        // When & Then
        mockMvc.perform(put("/api/tags/{id}", tagId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tagId))
                .andExpect(jsonPath("$.name").value("updated-tag"))
                .andExpect(jsonPath("$.color").value("#00FFFF"));
    }

    @Test
    void updateTag_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        Long tagId = 1L;
        UpdateTagRequest request = new UpdateTagRequest();
        // Empty request will trigger validation errors
        
        when(authentication.getName()).thenReturn("test@example.com");

        // When & Then
        mockMvc.perform(put("/api/tags/{id}", tagId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTag_ShouldReturnSuccessMessage() throws Exception {
        // Given
        String userEmail = "test@example.com";
        Long tagId = 1L;
        
        when(authentication.getName()).thenReturn(userEmail);

        // When & Then
        mockMvc.perform(delete("/api/tags/{id}", tagId)
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tag deleted successfully"));
    }

    private TagDTO createSampleTagDTO(Long id, String name, String color) {
        TagDTO tag = new TagDTO();
        tag.setId(id);
        tag.setName(name);
        tag.setColor(color);
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());
        return tag;
    }
} 