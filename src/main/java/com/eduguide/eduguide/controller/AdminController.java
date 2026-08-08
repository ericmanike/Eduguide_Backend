package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.*;
import com.eduguide.eduguide.repository.LearningPathRepository;
import com.eduguide.eduguide.repository.ModuleRepository;
import com.eduguide.eduguide.repository.PathModuleRepository;
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
    private final LearningPathRepository learningPathRepository;
    private final ModuleRepository moduleRepository;
    private final PathModuleRepository pathModuleRepository;

    public AdminController(
            UserService userService,
            LearningPathRepository learningPathRepository,
            ModuleRepository moduleRepository,
            PathModuleRepository pathModuleRepository
    ) {
        this.userService = userService;
        this.learningPathRepository = learningPathRepository;
        this.moduleRepository = moduleRepository;
        this.pathModuleRepository = pathModuleRepository;
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

    // Learning Path Management
    @GetMapping("/learning-paths")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all learning paths", description = "Admin only - retrieve all learning paths")
    public ResponseEntity<List<LearningPath>> getAllLearningPaths() {
        return ResponseEntity.ok(learningPathRepository.findAll());
    }

    @PostMapping("/learning-paths")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create learning path", description = "Admin only - create a new learning path")
    public ResponseEntity<?> createLearningPath(@RequestBody LearningPath learningPath) {
        if (learningPath.getTitle() == null || learningPath.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body("Error: Title is required!");
        }
        if (learningPath.getEstimatedHours() == null) {
            return ResponseEntity.badRequest().body("Error: Estimated hours is required!");
        }
        learningPath.setId(null);
        return ResponseEntity.ok(learningPathRepository.save(learningPath));
    }

    @PutMapping("/learning-paths/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update learning path", description = "Admin only - update an existing learning path")
    public ResponseEntity<?> updateLearningPath(@PathVariable UUID id, @RequestBody LearningPath request) {
        return learningPathRepository.findById(id)
                .map(path -> {
                    if (request.getTitle() != null && !request.getTitle().isBlank()) {
                        path.setTitle(request.getTitle());
                    }
                    if (request.getDescription() != null) {
                        path.setDescription(request.getDescription());
                    }
                    if (request.getLevel() != null) {
                        path.setLevel(request.getLevel());
                    }
                    if (request.getEstimatedHours() != null) {
                        path.setEstimatedHours(request.getEstimatedHours());
                    }
                    return ResponseEntity.ok(learningPathRepository.save(path));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/learning-paths/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete learning path", description = "Admin only - delete a learning path")
    public ResponseEntity<?> deleteLearningPath(@PathVariable UUID id) {
        if (!learningPathRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        learningPathRepository.deleteById(id);
        return ResponseEntity.ok("Learning path deleted successfully!");
    }

    // Module Management
    @GetMapping("/modules")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all modules", description = "Admin only - retrieve all modules")
    public ResponseEntity<List<com.eduguide.eduguide.model.Module>> getAllModules() {
        return ResponseEntity.ok(moduleRepository.findAll());
    }

    @PostMapping("/modules")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create module", description = "Admin only - create a new module")
    public ResponseEntity<?> createModule(@RequestBody com.eduguide.eduguide.model.Module module) {
        if (module.getTitle() == null || module.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body("Error: Title is required!");
        }
        if (module.getTopic() == null || module.getTopic().isBlank()) {
            return ResponseEntity.badRequest().body("Error: Topic is required!");
        }
        if (module.getDurationMinutes() == null) {
            return ResponseEntity.badRequest().body("Error: Duration minutes is required!");
        }
        module.setId(null);
        return ResponseEntity.ok(moduleRepository.save(module));
    }

    @PutMapping("/modules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update module", description = "Admin only - update an existing module")
    public ResponseEntity<?> updateModule(@PathVariable UUID id, @RequestBody com.eduguide.eduguide.model.Module request) {
        return moduleRepository.findById(id)
                .map(module -> {
                    if (request.getTitle() != null && !request.getTitle().isBlank()) {
                        module.setTitle(request.getTitle());
                    }
                    if (request.getTopic() != null && !request.getTopic().isBlank()) {
                        module.setTopic(request.getTopic());
                    }
                    if (request.getDescription() != null) {
                        module.setDescription(request.getDescription());
                    }
                    if (request.getDurationMinutes() != null) {
                        module.setDurationMinutes(request.getDurationMinutes());
                    }
                    return ResponseEntity.ok(moduleRepository.save(module));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/modules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete module", description = "Admin only - delete a module")
    public ResponseEntity<?> deleteModule(@PathVariable UUID id) {
        if (!moduleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        moduleRepository.deleteById(id);
        return ResponseEntity.ok("Module deleted successfully!");
    }

    // Path-Module Mapping Management
    @GetMapping("/path-modules")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all path-module mappings", description = "Admin only - retrieve all path-module mappings")
    public ResponseEntity<List<PathModule>> getAllPathModules() {
        return ResponseEntity.ok(pathModuleRepository.findAll());
    }

    @GetMapping("/path-modules/path/{pathId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get modules for a path", description = "Admin only - retrieve modules for a specific learning path")
    public ResponseEntity<List<PathModule>> getModulesByPath(@PathVariable UUID pathId) {
        return ResponseEntity.ok(pathModuleRepository.findByPathIdOrderBySequenceOrderAsc(pathId));
    }

    @PostMapping("/path-modules")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add module to path", description = "Admin only - add a module to a learning path")
    public ResponseEntity<?> createPathModule(@RequestBody PathModuleRequest request) {
        if (request.getPathId() == null || request.getModuleId() == null || request.getSequenceOrder() == null) {
            return ResponseEntity.badRequest().body("Error: pathId, moduleId, and sequenceOrder are required!");
        }

        var pathOptional = learningPathRepository.findById(request.getPathId());
        var moduleOptional = moduleRepository.findById(request.getModuleId());

        if (pathOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Learning path not found!");
        }
        if (moduleOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Module not found!");
        }

        PathModule pathModule = new PathModule();
        pathModule.setPath(pathOptional.get());
        pathModule.setModule(moduleOptional.get());
        pathModule.setSequenceOrder(request.getSequenceOrder());

        return ResponseEntity.ok(pathModuleRepository.save(pathModule));
    }

    @PutMapping("/path-modules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update path-module mapping", description = "Admin only - update sequence order of a module in a path")
    public ResponseEntity<?> updatePathModule(@PathVariable UUID id, @RequestBody PathModuleRequest request) {
        return pathModuleRepository.findById(id)
                .map(pathModule -> {
                    if (request.getSequenceOrder() != null) {
                        pathModule.setSequenceOrder(request.getSequenceOrder());
                    }
                    return ResponseEntity.ok(pathModuleRepository.save(pathModule));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/path-modules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove module from path", description = "Admin only - remove a module from a learning path")
    public ResponseEntity<?> deletePathModule(@PathVariable UUID id) {
        if (!pathModuleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pathModuleRepository.deleteById(id);
        return ResponseEntity.ok("Module removed from path successfully!");
    }

    // Inner class for stats response
    record SystemStats(long totalUsers) {}
}
