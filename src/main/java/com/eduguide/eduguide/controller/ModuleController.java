package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.Module;
import com.eduguide.eduguide.repository.ModuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleRepository moduleRepository;

    public ModuleController(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    @GetMapping
    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Module> getModule(@PathVariable UUID id) {
        return moduleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createModule(@RequestBody Module module) {
        if (module.getTitle() == null || module.getTitle().isBlank()
                || module.getTopic() == null || module.getTopic().isBlank()
                || module.getDurationMinutes() == null) {
            return ResponseEntity.badRequest().body("Error: title, topic, and durationMinutes are required!");
        }
        module.setId(null);
        return ResponseEntity.ok(moduleRepository.save(module));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateModule(@PathVariable UUID id, @RequestBody Module request) {
        return moduleRepository.findById(id)
                .map(module -> {
                    module.setTitle(request.getTitle());
                    module.setTopic(request.getTopic());
                    module.setDescription(request.getDescription());
                    module.setDurationMinutes(request.getDurationMinutes());
                    return ResponseEntity.ok(moduleRepository.save(module));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable UUID id) {
        if (!moduleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        moduleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
