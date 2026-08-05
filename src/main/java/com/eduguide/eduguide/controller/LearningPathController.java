package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.LearningPath;
import com.eduguide.eduguide.repository.LearningPathRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/learning-paths")
public class LearningPathController {

    private final LearningPathRepository learningPathRepository;

    public LearningPathController(LearningPathRepository learningPathRepository) {
        this.learningPathRepository = learningPathRepository;
    }

    @GetMapping
    public List<LearningPath> getAllLearningPaths() {
        return learningPathRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningPath> getLearningPath(@PathVariable UUID id) {
        return learningPathRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createLearningPath(@RequestBody LearningPath learningPath) {
        if (learningPath.getTitle() == null || learningPath.getTitle().isBlank()
                || learningPath.getEstimatedHours() == null) {
            return ResponseEntity.badRequest().body("Error: title and estimatedHours are required!");
        }
        learningPath.setId(null);
        return ResponseEntity.ok(learningPathRepository.save(learningPath));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLearningPath(@PathVariable UUID id, @RequestBody LearningPath request) {
        return learningPathRepository.findById(id)
                .map(path -> {
                    path.setTitle(request.getTitle());
                    path.setDescription(request.getDescription());
                    path.setLevel(request.getLevel());
                    path.setEstimatedHours(request.getEstimatedHours());
                    return ResponseEntity.ok(learningPathRepository.save(path));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLearningPath(@PathVariable UUID id) {
        if (!learningPathRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        learningPathRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
