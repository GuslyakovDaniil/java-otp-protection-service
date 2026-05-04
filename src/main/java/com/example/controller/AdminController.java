package com.example.controller;

import com.example.model.User;
import com.example.model.dto.ConfigUpdateRequest;
import com.example.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/config")
    public ResponseEntity<String> updateConfig(@RequestBody ConfigUpdateRequest request) {
        logger.info("PUT /api/admin/config - Updating OTP config");
        adminService.updateConfig(request);
        return ResponseEntity.ok("Configuration updated successfully");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        logger.info("GET /api/admin/users - Fetching all non-admin users");
        return ResponseEntity.ok(adminService.getAllUsersExceptAdmins());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        logger.info("DELETE /api/admin/users/{} - Deleting user", id);
        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
