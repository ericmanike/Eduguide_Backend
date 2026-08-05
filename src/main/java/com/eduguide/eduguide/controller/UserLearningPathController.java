package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.UserLearningPath;
import com.eduguide.eduguide.model.UserLearningPathRequest;
import com.eduguide.eduguide.repository.LearningPathRepository;
import com.eduguide.eduguide.repository.UserLearningPathRepository;
import com.eduguide.eduguide.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-learning-paths")
public class UserLearningPathController {

    private final UserLearningPathRepository userLearningPathRepository;
    private final UserRepository userRepository;
    private final LearningPathRepository learningPathRepository;

    public UserLearningPathController(
            UserLearningPathRepository userLearningPathRepository,
            UserRepository userRepository,
            LearningPathRepository learningPathRepository
    ) {
        this.userLearningPathRepository = userLearningPathRepository;
        this.userRepository = userRepository;
        this.learningPathRepository = learningPathRepository;
    }

    @GetMapping
    public List<UserLearningPath> getAllUserLearningPaths() {
        return userLearningPathRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<UserLearningPath> getUserLearningPaths(@PathVariable UUID userId) {
        return userLearningPathRepository.findByUserId(userId);
    }

    @GetMapping("/user/{userId}/active")
    public List<UserLearningPath> getActiveUserLearningPaths(@PathVariable UUID userId) {
        return userLearningPathRepository.findByUserIdAndActiveTrue(userId);
    }

    @PostMapping
    public ResponseEntity<?> createUserLearningPath(@RequestBody UserLearningPathRequest request) {
        if (request.getUserId() == null || request.getPathId() == null) {
            return ResponseEntity.badRequest().body("Error: userId and pathId are required!");
        }
        if (!validPercentage(request.getMatchScore()) || !validPercentage(request.getProgressPercentage())) {
            return ResponseEntity.badRequest().body("Error: matchScore and progressPercentage must be between 0 and 100!");
        }

        var userOptional = userRepository.findById(request.getUserId());
        var pathOptional = learningPathRepository.findById(request.getPathId());
        if (userOptional.isEmpty() || pathOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: User or learning path not found!");
        }

        UserLearningPath userLearningPath = new UserLearningPath();
        userLearningPath.setUser(userOptional.get());
        userLearningPath.setPath(pathOptional.get());
        userLearningPath.setActive(Boolean.TRUE.equals(request.getActive()));
        userLearningPath.setMatchScore(request.getMatchScore());
        userLearningPath.setProgressPercentage(request.getProgressPercentage() == null ? 0 : request.getProgressPercentage());
        return ResponseEntity.ok(userLearningPathRepository.save(userLearningPath));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserLearningPath(@PathVariable UUID id, @RequestBody UserLearningPathRequest request) {
        if (!validPercentage(request.getMatchScore()) || !validPercentage(request.getProgressPercentage())) {
            return ResponseEntity.badRequest().body("Error: matchScore and progressPercentage must be between 0 and 100!");
        }

        return userLearningPathRepository.findById(id)
                .map(userLearningPath -> {
                    if (request.getActive() != null) {
                        userLearningPath.setActive(request.getActive());
                    }
                    if (request.getMatchScore() != null) {
                        userLearningPath.setMatchScore(request.getMatchScore());
                    }
                    if (request.getProgressPercentage() != null) {
                        userLearningPath.setProgressPercentage(request.getProgressPercentage());
                    }
                    return ResponseEntity.ok(userLearningPathRepository.save(userLearningPath));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserLearningPath(@PathVariable UUID id) {
        if (!userLearningPathRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userLearningPathRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private boolean validPercentage(Integer value) {
        return value == null || (value >= 0 && value <= 100);
    }
}
