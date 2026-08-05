package com.eduguide.eduguide.repository;

import com.eduguide.eduguide.model.PathModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PathModuleRepository extends JpaRepository<PathModule, UUID> {
    List<PathModule> findByPathIdOrderBySequenceOrderAsc(UUID pathId);
}
