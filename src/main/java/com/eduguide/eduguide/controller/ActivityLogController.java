package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.ActivityLog;
import com.eduguide.eduguide.model.ActivityLogRequest;
import com.eduguide.eduguide.repository.ActivityLogRepository;
import com.eduguide.eduguide.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public ActivityLogController(ActivityLogRepository activityLogRepository, UserRepository userRepository) {
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<ActivityLog> getAllActivityLogs() {
        return activityLogRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<ActivityLog> getActivityLogsByUser(@PathVariable UUID userId) {
        return activityLogRepository.findByUserIdOrderByActivityDateDesc(userId);
    }

    @PostMapping
    public ResponseEntity<?> createActivityLog(@RequestBody ActivityLogRequest request) {
        if (request.getUserId() == null || request.getActivityDate() == null) {
            return ResponseEntity.badRequest().body("Error: userId and activityDate are required!");
        }

        var userOptional = userRepository.findById(request.getUserId());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: User not found!");
        }

        ActivityLog activityLog = new ActivityLog();
        activityLog.setUser(userOptional.get());
        activityLog.setActivityDate(request.getActivityDate());
        activityLog.setHoursSpent(request.getHoursSpent() == null ? BigDecimal.ZERO : request.getHoursSpent());
        return ResponseEntity.ok(activityLogRepository.save(activityLog));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivityLog(@PathVariable UUID id) {
        if (!activityLogRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        activityLogRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
