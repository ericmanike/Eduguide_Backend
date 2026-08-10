package com.eduguide.eduguide.repository;

import com.eduguide.eduguide.model.ProgressStatus;
import com.eduguide.eduguide.model.UserLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, UUID> {

    Optional<UserLessonProgress> findByUserIdAndLessonId(UUID userId, UUID lessonId);

    List<UserLessonProgress> findByUserId(UUID userId);

    List<UserLessonProgress> findByLessonId(UUID lessonId);

    @Query("SELECT ulp FROM UserLessonProgress ulp WHERE ulp.user.id = :userId AND ulp.lesson.module.id = :moduleId")
    List<UserLessonProgress> findByUserIdAndModuleId(UUID userId, UUID moduleId);

    @Query("SELECT COUNT(ulp) FROM UserLessonProgress ulp WHERE ulp.user.id = :userId AND ulp.lesson.module.id = :moduleId AND ulp.status = :status")
    long countByUserIdAndModuleIdAndStatus(UUID userId, UUID moduleId, ProgressStatus status);
}
