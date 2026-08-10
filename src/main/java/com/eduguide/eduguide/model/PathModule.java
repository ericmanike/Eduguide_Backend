package com.eduguide.eduguide.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private LearningPath path;

    @ManyToOne(optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    @JsonIgnoreProperties({"lessons", "hibernateLazyInitializer", "handler"})
    private Module module;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;
}
