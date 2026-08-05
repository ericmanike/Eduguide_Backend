package com.eduguide.eduguide.service;

import com.eduguide.eduguide.model.UserLearningPath;
import com.eduguide.eduguide.model.UserLearningPathRequest;
import com.eduguide.eduguide.repository.LearningPathRepository;
import com.eduguide.eduguide.repository.UserLearningPathRepository;
import com.eduguide.eduguide.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserLearningPathService {

    private final UserLearningPathRepository userLearningPathRepository;
    private final UserRepository userRepository;
    private final LearningPathRepository learningPathRepository;

    public UserLearningPathService(
            UserLearningPathRepository userLearningPathRepository,
            UserRepository userRepository,
            LearningPathRepository learningPathRepository
    ) {
        this.userLearningPathRepository = userLearningPathRepository;
        this.userRepository = userRepository;
        this.learningPathRepository = learningPathRepository;
    }

    public List<UserLearningPath> getAllUserLearningPaths() {
        return userLearningPathRepository.findAll();
    }

    public List<UserLearningPath> getUserLearningPathsByUserId(UUID userId) {
        return userLearningPathRepository.findByUserId(userId);
    }

    public List<UserLearningPath> getActiveUserLearningPathsByUserId(UUID userId) {
        return userLearningPathRepository.findByUserIdAndActiveTrue(userId);
    }

    public Optional<UserLearningPath> createUserLearningPath(UserLearningPathRequest request) {
        var userOptional = userRepository.findById(request.getUserId());
        var pathOptional = learningPathRepository.findById(request.getPathId());

        if (userOptional.isPresent() && pathOptional.isPresent()) {
            UserLearningPath userLearningPath = new UserLearningPath();
            userLearningPath.setUser(userOptional.get());
            userLearningPath.setPath(pathOptional.get());
            userLearningPath.setActive(Boolean.TRUE.equals(request.getActive()));
            userLearningPath.setMatchScore(request.getMatchScore());
            userLearningPath.setProgressPercentage(request.getProgressPercentage() == null ? 0 : request.getProgressPercentage());
            return Optional.of(userLearningPathRepository.save(userLearningPath));
        }
        return Optional.empty();
    }

    public Optional<UserLearningPath> updateUserLearningPath(UUID id, UserLearningPathRequest request) {
        return userLearningPathRepository.findById(id).map(userLearningPath -> {
            if (request.getActive() != null) {
                userLearningPath.setActive(request.getActive());
            }
            if (request.getMatchScore() != null) {
                userLearningPath.setMatchScore(request.getMatchScore());
            }
            if (request.getProgressPercentage() != null) {
                userLearningPath.setProgressPercentage(request.getProgressPercentage());
            }
            return userLearningPathRepository.save(userLearningPath);
        });
    }

    public boolean deleteUserLearningPath(UUID id) {
        if (userLearningPathRepository.existsById(id)) {
            userLearningPathRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
