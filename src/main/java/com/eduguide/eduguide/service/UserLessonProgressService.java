package com.eduguide.eduguide.service;

import com.eduguide.eduguide.model.*;
import com.eduguide.eduguide.repository.LessonRepository;
import com.eduguide.eduguide.repository.ModuleRepository;
import com.eduguide.eduguide.repository.UserLessonProgressRepository;
import com.eduguide.eduguide.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserLessonProgressService {

    private final UserLessonProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public UserLessonProgressService(UserLessonProgressRepository progressRepository,
                                     UserRepository userRepository,
                                     LessonRepository lessonRepository,
                                     ModuleRepository moduleRepository) {
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.moduleRepository = moduleRepository;
    }

    @Transactional
    public UserLessonProgress updateProgress(UUID userId, UUID lessonId, ProgressStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        UserLessonProgress progress = progressRepository
                .findByUserIdAndLessonId(userId, lessonId)
                .orElse(new UserLessonProgress());

        progress.setUser(user);
        progress.setLesson(lesson);
        progress.setStatus(status);

        if (status == ProgressStatus.COMPLETED && progress.getCompletedAt() == null) {
            progress.setCompletedAt(OffsetDateTime.now());
        }

        return progressRepository.save(progress);
    }

    public UserLessonProgress getProgress(UUID userId, UUID lessonId) {
        return progressRepository.findByUserIdAndLessonId(userId, lessonId)
                .orElse(null);
    }

    public List<UserLessonProgress> getUserProgress(UUID userId) {
        return progressRepository.findByUserId(userId);
    }

    public List<UserLessonProgress> getModuleProgress(UUID userId, UUID moduleId) {
        return progressRepository.findByUserIdAndModuleId(userId, moduleId);
    }

    public Map<String, Object> getModuleCompletionStats(UUID userId, UUID moduleId) {
        com.eduguide.eduguide.model.Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        long totalLessons = lessonRepository.findByModuleIdOrderBySequenceOrderAsc(moduleId).size();
        long completedLessons = progressRepository.countByUserIdAndModuleIdAndStatus(
                userId, moduleId, ProgressStatus.COMPLETED);
        long inProgressLessons = progressRepository.countByUserIdAndModuleIdAndStatus(
                userId, moduleId, ProgressStatus.IN_PROGRESS);

        double completionPercentage = totalLessons > 0
                ? (completedLessons * 100.0) / totalLessons
                : 0.0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("moduleId", moduleId);
        stats.put("moduleTitle", module.getTitle());
        stats.put("totalLessons", totalLessons);
        stats.put("completedLessons", completedLessons);
        stats.put("inProgressLessons", inProgressLessons);
        stats.put("notStartedLessons", totalLessons - completedLessons - inProgressLessons);
        stats.put("completionPercentage", Math.round(completionPercentage * 100.0) / 100.0);
        stats.put("isCompleted", completedLessons == totalLessons && totalLessons > 0);

        return stats;
    }
}
