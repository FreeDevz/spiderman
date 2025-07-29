package com.todoapp.service;

import com.todoapp.dto.TagDTO;
import com.todoapp.dto.CreateTagRequest;
import com.todoapp.dto.UpdateTagRequest;
import com.todoapp.entity.Tag;
import com.todoapp.entity.User;
import com.todoapp.exception.ResourceNotFoundException;
import com.todoapp.repository.TagRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private User testUser;
    private Tag urgentTag;
    private Tag meetingTag;
    private CreateTagRequest createRequest;
    private UpdateTagRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        urgentTag = new Tag();
        urgentTag.setId(1L);
        urgentTag.setName("urgent");
        urgentTag.setColor("#EF4444");
        urgentTag.setUser(testUser);

        meetingTag = new Tag();
        meetingTag.setId(2L);
        meetingTag.setName("meeting");
        meetingTag.setColor("#3B82F6");
        meetingTag.setUser(testUser);

        createRequest = new CreateTagRequest();
        createRequest.setName("new-tag");
        createRequest.setColor("#10B981");

        updateRequest = new UpdateTagRequest();
        updateRequest.setName("updated-tag");
        updateRequest.setColor("#F59E0B");
    }

    @Test
    void getTags_WithValidUser_ShouldReturnTags() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tagRepository.findByUserIdOrderByNameAsc(1L)).thenReturn(Arrays.asList(urgentTag, meetingTag));

        // When
        List<TagDTO> tags = tagService.getTags("test@example.com");

        // Then
        assertThat(tags).isNotNull();
        assertThat(tags).hasSize(2);
        assertThat(tags.get(0).getName()).isEqualTo("urgent");
        assertThat(tags.get(1).getName()).isEqualTo("meeting");

        verify(userRepository).findByEmail("test@example.com");
        verify(tagRepository).findByUserIdOrderByNameAsc(1L);
    }

    @Test
    void getTags_WithNoTags_ShouldReturnEmptyList() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tagRepository.findByUserIdOrderByNameAsc(1L)).thenReturn(Arrays.asList());

        // When
        List<TagDTO> tags = tagService.getTags("test@example.com");

        // Then
        assertThat(tags).isNotNull();
        assertThat(tags).isEmpty();

        verify(userRepository).findByEmail("test@example.com");
        verify(tagRepository).findByUserIdOrderByNameAsc(1L);
    }

    @Test
    void getTags_WithInvalidUser_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tagService.getTags("nonexistent@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void getAllTags_WithValidUser_ShouldReturnAllTags() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tagRepository.findByUserIdOrderByNameAsc(1L)).thenReturn(Arrays.asList(urgentTag, meetingTag));

        // When
        List<TagDTO> tags = tagService.getAllTags("test@example.com");

        // Then
        assertThat(tags).isNotNull();
        assertThat(tags).hasSize(2);

        verify(userRepository).findByEmail("test@example.com");
        verify(tagRepository).findByUserIdOrderByNameAsc(1L);
    }

    @Test
    void createTag_WithValidRequest_ShouldReturnCreatedTag() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> {
            Tag tag = invocation.getArgument(0);
            tag.setId(3L);
            return tag;
        });

        // When
        TagDTO createdTag = tagService.createTag(createRequest, "test@example.com");

        // Then
        assertThat(createdTag).isNotNull();
        assertThat(createdTag.getName()).isEqualTo("new-tag");
        assertThat(createdTag.getColor()).isEqualTo("#10B981");

        verify(userRepository).findByEmail("test@example.com");
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void createTag_WithInvalidUser_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tagService.createTag(createRequest, "nonexistent@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void updateTag_WithValidRequest_ShouldReturnUpdatedTag() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tagRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(urgentTag));
        when(tagRepository.save(any(Tag.class))).thenReturn(urgentTag);

        // When
        TagDTO updatedTag = tagService.updateTag(1L, updateRequest, "test@example.com");

        // Then
        assertThat(updatedTag).isNotNull();
        assertThat(updatedTag.getName()).isEqualTo("updated-tag");
        assertThat(updatedTag.getColor()).isEqualTo("#F59E0B");

        verify(userRepository).findByEmail("test@example.com");
        verify(tagRepository).findByIdAndUserId(1L, 1L);
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void updateTag_WithInvalidTagId_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tagRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tagService.updateTag(999L, updateRequest, "test@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tag not found");

        verify(userRepository).findByEmail("test@example.com");
        verify(tagRepository).findByIdAndUserId(999L, 1L);
    }

    @Test
    void updateTag_WithInvalidUser_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tagService.updateTag(1L, updateRequest, "nonexistent@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void deleteTag_WithValidId_ShouldDeleteTag() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tagRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(urgentTag));

        // When
        tagService.deleteTag(1L, "test@example.com");

        // Then
        verify(userRepository).findByEmail("test@example.com");
        verify(tagRepository).findByIdAndUserId(1L, 1L);
        verify(tagRepository).delete(urgentTag);
    }

    @Test
    void deleteTag_WithInvalidId_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tagRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tagService.deleteTag(999L, "test@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tag not found");

        verify(userRepository).findByEmail("test@example.com");
        verify(tagRepository).findByIdAndUserId(999L, 1L);
    }

    @Test
    void deleteTag_WithInvalidUser_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tagService.deleteTag(1L, "nonexistent@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void getTag_WithValidId_ShouldReturnTag() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tagRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(urgentTag));

        // When
        TagDTO tag = tagService.getTag(1L, "test@example.com");

        // Then
        assertThat(tag).isNotNull();
        assertThat(tag.getName()).isEqualTo("urgent");
        assertThat(tag.getColor()).isEqualTo("#EF4444");

        verify(userRepository).findByEmail("test@example.com");
        verify(tagRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    void getTag_WithInvalidId_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tagRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tagService.getTag(999L, "test@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tag not found");

        verify(userRepository).findByEmail("test@example.com");
        verify(tagRepository).findByIdAndUserId(999L, 1L);
    }

    @Test
    void getTag_WithInvalidUser_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tagService.getTag(1L, "nonexistent@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void createTag_WithNullName_ShouldThrowException() {
        // Given
        CreateTagRequest nullRequest = new CreateTagRequest();
        nullRequest.setName(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> tagService.createTag(nullRequest, "test@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tag name cannot be null or empty");

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void createTag_WithEmptyName_ShouldThrowException() {
        // Given
        createRequest.setName("");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> tagService.createTag(createRequest, "test@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tag name cannot be null or empty");

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void updateTag_WithNullName_ShouldThrowException() {
        // Given
        UpdateTagRequest updateRequest = new UpdateTagRequest();
        updateRequest.setName(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tagRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(urgentTag));

        // When & Then
        assertThatThrownBy(() -> tagService.updateTag(1L, updateRequest, "test@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tag name cannot be empty");

        verify(userRepository).findByEmail("test@example.com");
        verify(tagRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    void tagDTO_Mapping_ShouldBeCorrect() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tagRepository.findByUserIdOrderByNameAsc(1L)).thenReturn(Arrays.asList(urgentTag));

        // When
        List<TagDTO> tags = tagService.getTags("test@example.com");

        // Then
        assertThat(tags).hasSize(1);
        TagDTO tagDTO = tags.get(0);
        assertThat(tagDTO.getId()).isEqualTo(1L);
        assertThat(tagDTO.getName()).isEqualTo("urgent");
        assertThat(tagDTO.getColor()).isEqualTo("#EF4444");

        verify(userRepository).findByEmail("test@example.com");
        verify(tagRepository).findByUserIdOrderByNameAsc(1L);
    }

    @Test
    void createTag_WithDuplicateName_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tagRepository.existsByNameAndUserId(anyString(), anyLong())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> tagService.createTag(createRequest, "test@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tag with this name already exists");

        verify(userRepository).findByEmail("test@example.com");
        verify(tagRepository).existsByNameAndUserId(anyString(), anyLong());
    }

    @Test
    void updateTag_WithDuplicateName_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tagRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(urgentTag));
        when(tagRepository.existsByNameAndUserId("updated-tag", 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> tagService.updateTag(1L, updateRequest, "test@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tag with this name already exists");

        verify(userRepository).findByEmail("test@example.com");
        verify(tagRepository).findByIdAndUserId(1L, 1L);
        verify(tagRepository).existsByNameAndUserId("updated-tag", 1L);
    }
} 