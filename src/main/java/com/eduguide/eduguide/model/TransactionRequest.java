package com.eduguide.eduguide.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionRequest {
    private UUID userId;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
}
