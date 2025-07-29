package com.todoapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SimpleControllerTest {

    @InjectMocks
    private SimpleController simpleController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(simpleController).build();
    }

    @Test
    void hello_ShouldReturnHelloMessage() throws Exception {
        // When & Then
        mockMvc.perform(get("/simple/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello from SimpleController!"));
    }

    @Test
    void test_ShouldReturnTestResponse() throws Exception {
        // When & Then
        mockMvc.perform(get("/simple/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Test successful"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void simpleTest_ShouldReturnSimpleString() throws Exception {
        // When & Then
        mockMvc.perform(get("/simple/simple-test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Simple test successful"));
    }

    @Test
    void testResponseClass_ShouldHaveCorrectStructure() {
        // Given
        SimpleController.TestResponse response = new SimpleController.TestResponse("Test message", 100, false);

        // When & Then
        assert response.getMessage().equals("Test message");
        assert response.getCode() == 100;
        assert !response.isSuccess();

        // Test setters
        response.setMessage("Updated message");
        response.setCode(200);
        response.setSuccess(true);

        assert response.getMessage().equals("Updated message");
        assert response.getCode() == 200;
        assert response.isSuccess();
    }
} 