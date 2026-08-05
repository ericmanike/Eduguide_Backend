package com.eduguide.eduguide.model;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UserModuleProgressRequest {
    private UUID userId;
    private UUID moduleId;
    private ProgressStatus status;
    private OffsetDateTime completedAt;
}
