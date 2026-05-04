package com.example.controller;

import com.example.model.dto.AuthRequest;
import com.example.model.dto.AuthResponse;
import com.example.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        logger.info("POST /api/auth/register - Request for user: {}", request.getUsername());
        try {
            authService.register(request);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            logger.error("Registration failed", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        logger.info("POST /api/auth/login - Request for user: {}", request.getUsername());
        try {
            AuthResponse token = authService.login(request);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            logger.error("Login failed", e);
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
