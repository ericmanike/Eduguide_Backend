package com.eduguide.eduguide.model;

import lombok.Data;

import java.util.UUID;

@Data
public class UserSkillRequest {
    private UUID userId;
    private UUID skillId;
    private Integer masteryLevel;
}
