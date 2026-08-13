package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.ProgressStatus;
import com.eduguide.eduguide.model.UserLessonProgress;
import com.eduguide.eduguide.service.UserLessonProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-lesson-progress")
public class UserLessonProgressController {

    private final UserLessonProgressService progressService;

    public UserLessonProgressController(UserLessonProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping
    public ResponseEntity<?> updateProgress(@RequestBody Map<String, String> request) {
        try {
            UUID userId = UUID.fromString(request.get("userId"));
            UUID lessonId = UUID.fromString(request.get("lessonId"));
            ProgressStatus status = ProgressStatus.valueOf(request.get("status"));

            UserLessonProgress progress = progressService.updateProgress(userId, lessonId, status);
            return ResponseEntity.ok(progress);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid request parameters: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/lesson/{lessonId}")
    public ResponseEntity<UserLessonProgress> getProgress(@PathVariable UUID userId, @PathVariable UUID lessonId) {
        UserLessonProgress progress = progressService.getProgress(userId, lessonId);
        if (progress == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserLessonProgress>> getUserProgress(@PathVariable UUID userId) {
        return ResponseEntity.ok(progressService.getUserProgress(userId));
    }

    @GetMapping("/user/{userId}/module/{moduleId}")
    public ResponseEntity<List<UserLessonProgress>> getModuleProgress(@PathVariable UUID userId, @PathVariable UUID moduleId) {
        return ResponseEntity.ok(progressService.getModuleProgress(userId, moduleId));
    }

    @GetMapping("/user/{userId}/module/{moduleId}/stats")
    public ResponseEntity<Map<String, Object>> getModuleCompletionStats(@PathVariable UUID userId, @PathVariable UUID moduleId) {
        try {
            Map<String, Object> stats = progressService.getModuleCompletionStats(userId, moduleId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/path/{pathId}/stats")
    public ResponseEntity<Map<String, Object>> getPathCompletionStats(@PathVariable UUID userId, @PathVariable UUID pathId) {
        try {
            Map<String, Object> stats = progressService.getPathCompletionStats(userId, pathId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
