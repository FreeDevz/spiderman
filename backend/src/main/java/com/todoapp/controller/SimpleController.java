package com.todoapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Simple controller without dependencies to test dependency injection.
 */
@RestController
@RequestMapping("/simple")
public class SimpleController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello from SimpleController!");
    }
} 