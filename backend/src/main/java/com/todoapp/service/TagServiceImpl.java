package com.todoapp.service;

import com.todoapp.dto.TagDTO;
import com.todoapp.dto.CreateTagRequest;
import com.todoapp.dto.UpdateTagRequest;
import com.todoapp.entity.Tag;
import com.todoapp.entity.User;
import com.todoapp.repository.TagRepository;
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
 * Service implementation for tag operations.
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<TagDTO> getTags(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<Tag> tags = tagRepository.findByUserIdOrderByNameAsc(user.getId());
        return tags.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<TagDTO> getAllTags(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<Tag> tags = tagRepository.findByUserIdOrderByNameAsc(user.getId());
        return tags.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public TagDTO createTag(CreateTagRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);
        
        // Check if tag with same name already exists
        if (tagRepository.existsByNameAndUserId(request.getName(), user.getId())) {
            throw new RuntimeException("Tag with this name already exists");
        }
        
        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setColor(request.getColor());
        tag.setUser(user);
        
        Tag savedTag = tagRepository.save(tag);
        return convertToDTO(savedTag);
    }

    @Override
    public TagDTO updateTag(Long id, UpdateTagRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);
        Tag tag = tagRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new RuntimeException("Tag not found"));
        
        // Check if new name conflicts with existing tag
        if (request.getName() != null && !request.getName().equals(tag.getName())) {
            if (tagRepository.existsByNameAndUserId(request.getName(), user.getId())) {
                throw new RuntimeException("Tag with this name already exists");
            }
            tag.setName(request.getName());
        }
        
        if (request.getColor() != null) {
            tag.setColor(request.getColor());
        }
        
        Tag updatedTag = tagRepository.save(tag);
        return convertToDTO(updatedTag);
    }

    @Override
    public void deleteTag(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);
        Tag tag = tagRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new RuntimeException("Tag not found"));
        
        // Check if tag is used by any tasks
        if (!tag.getTasks().isEmpty()) {
            throw new RuntimeException("Cannot delete tag that is used by tasks");
        }
        
        tagRepository.delete(tag);
    }

    @Override
    public TagDTO getTag(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);
        Tag tag = tagRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new RuntimeException("Tag not found"));
        return convertToDTO(tag);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private TagDTO convertToDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setColor(tag.getColor());
        dto.setCreatedAt(tag.getCreatedAt());
        dto.setUpdatedAt(tag.getUpdatedAt());
        dto.setTaskCount(tag.getTasks() != null ? tag.getTasks().size() : 0);
        return dto;
    }
} 