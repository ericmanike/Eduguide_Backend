package com.eduguide.eduguide.model;

import lombok.Data;

import java.util.UUID;

@Data
public class PathModuleRequest {
    private UUID pathId;
    private UUID moduleId;
    private Integer sequenceOrder;
}
