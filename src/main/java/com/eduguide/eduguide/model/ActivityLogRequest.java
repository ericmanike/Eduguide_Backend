package com.eduguide.eduguide.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ActivityLogRequest {
    private UUID userId;
    private LocalDate activityDate;
    private BigDecimal hoursSpent;
}
