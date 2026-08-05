package com.eduguide.eduguide.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_learning_paths",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "path_id"})
)
@Data
public class UserLearningPath {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "path_id", nullable = false)
    private LearningPath path;

    @Column(name = "is_active")
    private Boolean active = false;

    @Column(name = "match_score")
    private Integer matchScore;

    @Column(name = "progress_percentage")
    private Integer progressPercentage = 0;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
