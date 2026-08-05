package com.eduguide.eduguide.repository;

import com.eduguide.eduguide.model.UserLearningPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserLearningPathRepository extends JpaRepository<UserLearningPath, UUID> {
    List<UserLearningPath> findByUserId(UUID userId);
    List<UserLearningPath> findByUserIdAndActiveTrue(UUID userId);
}
