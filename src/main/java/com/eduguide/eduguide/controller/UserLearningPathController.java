package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.UserLearningPath;
import com.eduguide.eduguide.model.UserLearningPathRequest;
import com.eduguide.eduguide.service.UserLearningPathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-learning-paths")
public class UserLearningPathController {

    private final UserLearningPathService userLearningPathService;

    public UserLearningPathController(UserLearningPathService userLearningPathService) {
        this.userLearningPathService = userLearningPathService;
    }

    @GetMapping
    public List<UserLearningPath> getAllUserLearningPaths() {
        return userLearningPathService.getAllUserLearningPaths();
    }

    @GetMapping("/user/{userId}")
    public List<UserLearningPath> getUserLearningPaths(@PathVariable UUID userId) {
        return userLearningPathService.getUserLearningPathsByUserId(userId);
    }

    @GetMapping("/user/{userId}/active")
    public List<UserLearningPath> getActiveUserLearningPaths(@PathVariable UUID userId) {
        return userLearningPathService.getActiveUserLearningPathsByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<?> createUserLearningPath(@RequestBody UserLearningPathRequest request) {
        if (request.getUserId() == null || request.getPathId() == null) {
            return ResponseEntity.badRequest().body("Error: userId and pathId are required!");
        }
        if (!validPercentage(request.getMatchScore()) || !validPercentage(request.getProgressPercentage())) {
            return ResponseEntity.badRequest().body("Error: matchScore and progressPercentage must be between 0 and 100!");
        }

        return userLearningPathService.createUserLearningPath(request)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body("Error: User or learning path not found!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserLearningPath(@PathVariable UUID id, @RequestBody UserLearningPathRequest request) {
        if (!validPercentage(request.getMatchScore()) || !validPercentage(request.getProgressPercentage())) {
            return ResponseEntity.badRequest().body("Error: matchScore and progressPercentage must be between 0 and 100!");
        }

        return userLearningPathService.updateUserLearningPath(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserLearningPath(@PathVariable UUID id) {
        if (userLearningPathService.deleteUserLearningPath(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private boolean validPercentage(Integer value) {
        return value == null || (value >= 0 && value <= 100);
    }
}
