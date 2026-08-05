package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.ProgressStatus;
import com.eduguide.eduguide.model.UserModuleProgress;
import com.eduguide.eduguide.model.UserModuleProgressRequest;
import com.eduguide.eduguide.repository.ModuleRepository;
import com.eduguide.eduguide.repository.UserModuleProgressRepository;
import com.eduguide.eduguide.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-module-progress")
public class UserModuleProgressController {

    private final UserModuleProgressRepository userModuleProgressRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;

    public UserModuleProgressController(
            UserModuleProgressRepository userModuleProgressRepository,
            UserRepository userRepository,
            ModuleRepository moduleRepository
    ) {
        this.userModuleProgressRepository = userModuleProgressRepository;
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
    }

    @GetMapping
    public List<UserModuleProgress> getAllProgress() {
        return userModuleProgressRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<UserModuleProgress> getProgressByUser(@PathVariable UUID userId) {
        return userModuleProgressRepository.findByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<?> createProgress(@RequestBody UserModuleProgressRequest request) {
        if (request.getUserId() == null || request.getModuleId() == null) {
            return ResponseEntity.badRequest().body("Error: userId and moduleId are required!");
        }

        var userOptional = userRepository.findById(request.getUserId());
        var moduleOptional = moduleRepository.findById(request.getModuleId());
        if (userOptional.isEmpty() || moduleOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: User or module not found!");
        }

        UserModuleProgress progress = new UserModuleProgress();
        progress.setUser(userOptional.get());
        progress.setModule(moduleOptional.get());
        progress.setStatus(request.getStatus() == null ? ProgressStatus.NOT_STARTED : request.getStatus());
        progress.setCompletedAt(completedAtFor(progress.getStatus(), request.getCompletedAt()));
        return ResponseEntity.ok(userModuleProgressRepository.save(progress));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProgress(@PathVariable UUID id, @RequestBody UserModuleProgressRequest request) {
        return userModuleProgressRepository.findById(id)
                .map(progress -> {
                    if (request.getStatus() != null) {
                        progress.setStatus(request.getStatus());
                    }
                    progress.setCompletedAt(completedAtFor(progress.getStatus(), request.getCompletedAt()));
                    return ResponseEntity.ok(userModuleProgressRepository.save(progress));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgress(@PathVariable UUID id) {
        if (!userModuleProgressRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userModuleProgressRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private OffsetDateTime completedAtFor(ProgressStatus status, OffsetDateTime requestedCompletedAt) {
        if (status == ProgressStatus.COMPLETED) {
            return requestedCompletedAt == null ? OffsetDateTime.now() : requestedCompletedAt;
        }
        return requestedCompletedAt;
    }
}
