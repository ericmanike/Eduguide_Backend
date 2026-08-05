package com.eduguide.eduguide.service;

import com.eduguide.eduguide.model.ProgressStatus;
import com.eduguide.eduguide.model.UserModuleProgress;
import com.eduguide.eduguide.model.UserModuleProgressRequest;
import com.eduguide.eduguide.repository.ModuleRepository;
import com.eduguide.eduguide.repository.UserModuleProgressRepository;
import com.eduguide.eduguide.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserModuleProgressService {

    private final UserModuleProgressRepository userModuleProgressRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;

    public UserModuleProgressService(
            UserModuleProgressRepository userModuleProgressRepository,
            UserRepository userRepository,
            ModuleRepository moduleRepository
    ) {
        this.userModuleProgressRepository = userModuleProgressRepository;
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
    }

    public List<UserModuleProgress> getAllProgress() {
        return userModuleProgressRepository.findAll();
    }

    public List<UserModuleProgress> getProgressByUserId(UUID userId) {
        return userModuleProgressRepository.findByUserId(userId);
    }

    public Optional<UserModuleProgress> createProgress(UserModuleProgressRequest request) {
        var userOptional = userRepository.findById(request.getUserId());
        var moduleOptional = moduleRepository.findById(request.getModuleId());

        if (userOptional.isPresent() && moduleOptional.isPresent()) {
            UserModuleProgress progress = new UserModuleProgress();
            progress.setUser(userOptional.get());
            progress.setModule(moduleOptional.get());
            progress.setStatus(request.getStatus() == null ? ProgressStatus.NOT_STARTED : request.getStatus());
            progress.setCompletedAt(completedAtFor(progress.getStatus(), request.getCompletedAt()));
            return Optional.of(userModuleProgressRepository.save(progress));
        }
        return Optional.empty();
    }

    public Optional<UserModuleProgress> updateProgress(UUID id, UserModuleProgressRequest request) {
        return userModuleProgressRepository.findById(id).map(progress -> {
            if (request.getStatus() != null) {
                progress.setStatus(request.getStatus());
            }
            progress.setCompletedAt(completedAtFor(progress.getStatus(), request.getCompletedAt()));
            return userModuleProgressRepository.save(progress);
        });
    }

    public boolean deleteProgress(UUID id) {
        if (userModuleProgressRepository.existsById(id)) {
            userModuleProgressRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private OffsetDateTime completedAtFor(ProgressStatus status, OffsetDateTime requestedCompletedAt) {
        if (status == ProgressStatus.COMPLETED) {
            return requestedCompletedAt == null ? OffsetDateTime.now() : requestedCompletedAt;
        }
        return requestedCompletedAt;
    }
}
