package com.todoapp.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateUserRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        // When
        UpdateUserRequest request = new UpdateUserRequest();

        // Then
        assertNotNull(request);
        assertNull(request.getName());
        assertNull(request.getEmail());
        assertNull(request.getAvatarUrl());
        assertNull(request.getFirstName());
        assertNull(request.getLastName());
    }

    @Test
    void testParameterizedConstructor() {
        // Given
        String name = "John Doe";
        String email = "john@example.com";

        // When
        UpdateUserRequest request = new UpdateUserRequest(name, email);

        // Then
        assertEquals(name, request.getName());
        assertEquals(email, request.getEmail());
        assertNull(request.getAvatarUrl());
        assertNull(request.getFirstName());
        assertNull(request.getLastName());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        String name = "Jane Doe";
        String email = "jane@example.com";
        String avatarUrl = "https://example.com/avatar.jpg";
        String firstName = "Jane";
        String lastName = "Doe";

        // When
        request.setName(name);
        request.setEmail(email);
        request.setAvatarUrl(avatarUrl);
        request.setFirstName(firstName);
        request.setLastName(lastName);

        // Then
        assertEquals(name, request.getName());
        assertEquals(email, request.getEmail());
        assertEquals(avatarUrl, request.getAvatarUrl());
        assertEquals(firstName, request.getFirstName());
        assertEquals(lastName, request.getLastName());
    }

    @Test
    void testValidEmail() {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("valid@example.com");

        // When
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidEmail() {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("invalid-email");

        // When
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testNameTooLong() {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("a".repeat(101)); // 101 characters, exceeds max of 100

        // When
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testAvatarUrlTooLong() {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setAvatarUrl("a".repeat(501)); // 501 characters, exceeds max of 500

        // When
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("avatarUrl")));
    }

    @Test
    void testFirstNameTooLong() {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("a".repeat(51)); // 51 characters, exceeds max of 50

        // When
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("firstName")));
    }

    @Test
    void testLastNameTooLong() {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setLastName("a".repeat(51)); // 51 characters, exceeds max of 50

        // When
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("lastName")));
    }

    @Test
    void testValidRequest() {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setAvatarUrl("https://example.com/avatar.jpg");
        request.setFirstName("John");
        request.setLastName("Doe");

        // When
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }
} 