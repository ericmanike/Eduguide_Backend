package com.eduguide.eduguide.service;

import com.eduguide.eduguide.model.ActivityLog;
import com.eduguide.eduguide.model.ActivityLogRequest;
import com.eduguide.eduguide.repository.ActivityLogRepository;
import com.eduguide.eduguide.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository, UserRepository userRepository) {
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
    }

    public List<ActivityLog> getAllActivityLogs() {
        return activityLogRepository.findAll();
    }

    public List<ActivityLog> getActivityLogsByUserId(UUID userId) {
        return activityLogRepository.findByUserIdOrderByActivityDateDesc(userId);
    }

    public Optional<ActivityLog> createActivityLog(ActivityLogRequest request) {
        var userOptional = userRepository.findById(request.getUserId());
        if (userOptional.isPresent()) {
            ActivityLog activityLog = new ActivityLog();
            activityLog.setUser(userOptional.get());
            activityLog.setActivityDate(request.getActivityDate());
            activityLog.setHoursSpent(request.getHoursSpent() == null ? BigDecimal.ZERO : request.getHoursSpent());
            return Optional.of(activityLogRepository.save(activityLog));
        }
        return Optional.empty();
    }

    public boolean deleteActivityLog(UUID id) {
        if (activityLogRepository.existsById(id)) {
            activityLogRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
