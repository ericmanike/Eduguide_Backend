package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.User;
import com.eduguide.eduguide.model.RegisterRequest;
import com.eduguide.eduguide.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Admin", description = "Admin-only endpoints - requires ADMIN role")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Admin only - retrieve all users in the system")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Admin only - retrieve a specific user by ID")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Admin only - update any user's information")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody RegisterRequest request) {
        return userService.updateUser(id, request)
                .map(user -> ResponseEntity.ok("User updated successfully!"))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Admin only - delete a user from the system")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok("User deleted successfully!");
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change user role", description = "Admin only - change a user's role")
    public ResponseEntity<?> changeUserRole(@PathVariable UUID id, @RequestBody RegisterRequest request) {
        if (request.getRole() == null) {
            return ResponseEntity.badRequest().body("Error: Role is required!");
        }
        return userService.updateUser(id, request)
                .map(user -> ResponseEntity.ok("User role updated successfully!"))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get system statistics", description = "Admin only - get statistics about the system")
    public ResponseEntity<?> getSystemStats() {
        long totalUsers = userService.getAllUsers().size();
        return ResponseEntity.ok(new SystemStats(totalUsers));
    }

    // Inner class for stats response
    record SystemStats(long totalUsers) {}
}
