package com.eduguide.eduguide.model;

import lombok.Data;

import java.util.UUID;

@Data
public class UserLearningPathRequest {
    private UUID userId;
    private UUID pathId;
    private Boolean active;
    private Integer matchScore;
    private Integer progressPercentage;
}
