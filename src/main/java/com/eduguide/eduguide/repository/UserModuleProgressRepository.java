package com.eduguide.eduguide.repository;

import com.eduguide.eduguide.model.UserModuleProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserModuleProgressRepository extends JpaRepository<UserModuleProgress, UUID> {
    List<UserModuleProgress> findByUserId(UUID userId);
}
