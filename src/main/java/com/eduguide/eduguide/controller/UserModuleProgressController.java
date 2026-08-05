package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.UserModuleProgress;
import com.eduguide.eduguide.model.UserModuleProgressRequest;
import com.eduguide.eduguide.service.UserModuleProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-module-progress")
public class UserModuleProgressController {

    private final UserModuleProgressService userModuleProgressService;

    public UserModuleProgressController(UserModuleProgressService userModuleProgressService) {
        this.userModuleProgressService = userModuleProgressService;
    }

    @GetMapping
    public List<UserModuleProgress> getAllProgress() {
        return userModuleProgressService.getAllProgress();
    }

    @GetMapping("/user/{userId}")
    public List<UserModuleProgress> getProgressByUser(@PathVariable UUID userId) {
        return userModuleProgressService.getProgressByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<?> createProgress(@RequestBody UserModuleProgressRequest request) {
        if (request.getUserId() == null || request.getModuleId() == null) {
            return ResponseEntity.badRequest().body("Error: userId and moduleId are required!");
        }

        return userModuleProgressService.createProgress(request)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body("Error: User or module not found!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProgress(@PathVariable UUID id, @RequestBody UserModuleProgressRequest request) {
        return userModuleProgressService.updateProgress(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgress(@PathVariable UUID id) {
        if (userModuleProgressService.deleteProgress(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
