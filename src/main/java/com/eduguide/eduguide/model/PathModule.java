package com.eduguide.eduguide.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(
        name = "path_modules",
        uniqueConstraints = @UniqueConstraint(columnNames = {"path_id", "module_id"})
)
@Data
public class PathModule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "path_id", nullable = false)
    private LearningPath path;

    @ManyToOne(optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;
}
