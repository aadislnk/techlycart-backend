package com.techlycart.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
@RestController
public class HealthController {

//    @GetMapping("/health")
//    public String health() {
//        return "TechlyCart Backend is running";
//    }
@GetMapping("/")
public Map<String, String> root() {
    return Map.of(
            "message", "Welcome to TechlyCart Backend"
    );
}

    @GetMapping("/health")
    public Map<String, String> health() { //this method now returns an object
        return Map.of(
                "status", "UP",
                "service", "TechlyCart Backend"
        );
    }
}
