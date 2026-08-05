package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.UserSkill;
import com.eduguide.eduguide.model.UserSkillRequest;
import com.eduguide.eduguide.repository.SkillRepository;
import com.eduguide.eduguide.repository.UserRepository;
import com.eduguide.eduguide.repository.UserSkillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-skills")
public class UserSkillController {

    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public UserSkillController(
            UserSkillRepository userSkillRepository,
            UserRepository userRepository,
            SkillRepository skillRepository
    ) {
        this.userSkillRepository = userSkillRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    @GetMapping
    public List<UserSkill> getAllUserSkills() {
        return userSkillRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<UserSkill> getUserSkills(@PathVariable UUID userId) {
        return userSkillRepository.findByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<?> createUserSkill(@RequestBody UserSkillRequest request) {
        if (request.getUserId() == null || request.getSkillId() == null) {
            return ResponseEntity.badRequest().body("Error: userId and skillId are required!");
        }
        if (!validMastery(request.getMasteryLevel())) {
            return ResponseEntity.badRequest().body("Error: masteryLevel must be between 1 and 10!");
        }

        var userOptional = userRepository.findById(request.getUserId());
        var skillOptional = skillRepository.findById(request.getSkillId());
        if (userOptional.isEmpty() || skillOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: User or skill not found!");
        }

        UserSkill userSkill = new UserSkill();
        userSkill.setUser(userOptional.get());
        userSkill.setSkill(skillOptional.get());
        userSkill.setMasteryLevel(request.getMasteryLevel());
        return ResponseEntity.ok(userSkillRepository.save(userSkill));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserSkill(@PathVariable UUID id, @RequestBody UserSkillRequest request) {
        if (!validMastery(request.getMasteryLevel())) {
            return ResponseEntity.badRequest().body("Error: masteryLevel must be between 1 and 10!");
        }

        return userSkillRepository.findById(id)
                .map(userSkill -> {
                    userSkill.setMasteryLevel(request.getMasteryLevel());
                    return ResponseEntity.ok(userSkillRepository.save(userSkill));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserSkill(@PathVariable UUID id) {
        if (!userSkillRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userSkillRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private boolean validMastery(Integer value) {
        return value == null || (value >= 1 && value <= 10);
    }
}
