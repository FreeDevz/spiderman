package com.todoapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Simple controller without dependencies to test dependency injection.
 */
@RestController
@RequestMapping("/simple")
@Tag(name = "Simple", description = "Simple test endpoints")
public class SimpleController {

    @Operation(
        summary = "Simple hello endpoint",
        description = "Returns a simple hello message for testing purposes"
    )
    @ApiResponse(responseCode = "200", description = "Hello message returned successfully")
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello from SimpleController!");
    }

    @Operation(
        summary = "Test endpoint with object",
        description = "Returns a test object to verify OpenAPI schema generation"
    )
    @ApiResponse(responseCode = "200", description = "Test object returned successfully")
    @GetMapping("/test")
    public TestResponse test() {
        return new TestResponse("Test successful", 200, true);
    }

    @Operation(
        summary = "Test endpoint with simple response",
        description = "Returns a simple string response"
    )
    @ApiResponse(responseCode = "200", description = "Simple response returned successfully")
    @GetMapping("/simple-test")
    public String simpleTest() {
        return "Simple test successful";
    }

    @Schema(description = "Test response object")
    public static class TestResponse {
        @Schema(description = "Response message", example = "Test successful")
        private String message;
        @Schema(description = "Response code", example = "200")
        private int code;
        @Schema(description = "Success indicator", example = "true")
        private boolean success;

        public TestResponse(String message, int code, boolean success) {
            this.message = message;
            this.code = code;
            this.success = success;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
    }
} 