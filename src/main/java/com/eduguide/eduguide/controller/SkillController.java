package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.Skill;
import com.eduguide.eduguide.repository.SkillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillRepository skillRepository;

    public SkillController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @GetMapping
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Skill> getSkill(@PathVariable UUID id) {
        return skillRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createSkill(@RequestBody Skill skill) {
        if (skill.getName() == null || skill.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Error: name is required!");
        }
        if (skillRepository.findByName(skill.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Skill already exists!");
        }
        skill.setId(null);
        return ResponseEntity.ok(skillRepository.save(skill));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSkill(@PathVariable UUID id, @RequestBody Skill request) {
        return skillRepository.findById(id)
                .map(skill -> {
                    skill.setName(request.getName());
                    return ResponseEntity.ok(skillRepository.save(skill));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable UUID id) {
        if (!skillRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        skillRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
