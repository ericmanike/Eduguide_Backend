package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.PathModule;
import com.eduguide.eduguide.model.PathModuleRequest;
import com.eduguide.eduguide.repository.LearningPathRepository;
import com.eduguide.eduguide.repository.ModuleRepository;
import com.eduguide.eduguide.repository.PathModuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/path-modules")
public class PathModuleController {

    private final PathModuleRepository pathModuleRepository;
    private final LearningPathRepository learningPathRepository;
    private final ModuleRepository moduleRepository;

    public PathModuleController(
            PathModuleRepository pathModuleRepository,
            LearningPathRepository learningPathRepository,
            ModuleRepository moduleRepository
    ) {
        this.pathModuleRepository = pathModuleRepository;
        this.learningPathRepository = learningPathRepository;
        this.moduleRepository = moduleRepository;
    }

    @GetMapping
    public List<PathModule> getAllPathModules() {
        return pathModuleRepository.findAll();
    }

    @GetMapping("/path/{pathId}")
    public List<PathModule> getModulesByPath(@PathVariable UUID pathId) {
        return pathModuleRepository.findByPathIdOrderBySequenceOrderAsc(pathId);
    }

    @PostMapping
    public ResponseEntity<?> createPathModule(@RequestBody PathModuleRequest request) {
        if (request.getPathId() == null || request.getModuleId() == null || request.getSequenceOrder() == null) {
            return ResponseEntity.badRequest().body("Error: pathId, moduleId, and sequenceOrder are required!");
        }

        var pathOptional = learningPathRepository.findById(request.getPathId());
        var moduleOptional = moduleRepository.findById(request.getModuleId());
        if (pathOptional.isEmpty() || moduleOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Path or module not found!");
        }

        PathModule pathModule = new PathModule();
        pathModule.setPath(pathOptional.get());
        pathModule.setModule(moduleOptional.get());
        pathModule.setSequenceOrder(request.getSequenceOrder());
        return ResponseEntity.ok(pathModuleRepository.save(pathModule));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePathModule(@PathVariable UUID id, @RequestBody PathModuleRequest request) {
        return pathModuleRepository.findById(id)
                .map(pathModule -> {
                    if (request.getSequenceOrder() != null) {
                        pathModule.setSequenceOrder(request.getSequenceOrder());
                    }
                    return ResponseEntity.ok(pathModuleRepository.save(pathModule));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePathModule(@PathVariable UUID id) {
        if (!pathModuleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pathModuleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
