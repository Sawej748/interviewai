package com.interviewAi.Backend.controler;

import com.interviewAi.Backend.dto.AdminStatsResponse;
import com.interviewAi.Backend.entity.Interview;
import com.interviewAi.Backend.entity.User;
import com.interviewAi.Backend.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Platform Metrics
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getPlatformStats());
    }

    // Get All Users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // Change User Role
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<User> changeUserRole(
            @PathVariable Long userId,
            @RequestBody Map<String, String> payload) {
        String role = payload.getOrDefault("role", "USER");
        return ResponseEntity.ok(adminService.updateUserRole(userId, role));
    }

    // Delete User
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // Get All Sessions
    @GetMapping("/interviews")
    public ResponseEntity<List<Interview>> getAllInterviews() {
        return ResponseEntity.ok(adminService.getAllInterviews());
    }
}