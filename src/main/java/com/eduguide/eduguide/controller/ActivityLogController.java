package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.ActivityLog;
import com.eduguide.eduguide.model.ActivityLogRequest;
import com.eduguide.eduguide.service.ActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public List<ActivityLog> getAllActivityLogs() {
        return activityLogService.getAllActivityLogs();
    }

    @GetMapping("/user/{userId}")
    public List<ActivityLog> getActivityLogsByUser(@PathVariable UUID userId) {
        return activityLogService.getActivityLogsByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<?> createActivityLog(@RequestBody ActivityLogRequest request) {
        if (request.getUserId() == null || request.getActivityDate() == null) {
            return ResponseEntity.badRequest().body("Error: userId and activityDate are required!");
        }

        return activityLogService.createActivityLog(request)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body("Error: User not found!"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivityLog(@PathVariable UUID id) {
        if (activityLogService.deleteActivityLog(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
