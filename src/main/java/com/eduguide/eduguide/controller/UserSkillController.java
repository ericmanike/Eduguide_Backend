package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.UserSkill;
import com.eduguide.eduguide.model.UserSkillRequest;
import com.eduguide.eduguide.service.UserSkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-skills")
public class UserSkillController {

    private final UserSkillService userSkillService;

    public UserSkillController(UserSkillService userSkillService) {
        this.userSkillService = userSkillService;
    }

    @GetMapping
    public List<UserSkill> getAllUserSkills() {
        return userSkillService.getAllUserSkills();
    }

    @GetMapping("/user/{userId}")
    public List<UserSkill> getUserSkills(@PathVariable UUID userId) {
        return userSkillService.getUserSkillsByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<?> createUserSkill(@RequestBody UserSkillRequest request) {
        if (request.getUserId() == null || request.getSkillId() == null) {
            return ResponseEntity.badRequest().body("Error: userId and skillId are required!");
        }
        if (!validMastery(request.getMasteryLevel())) {
            return ResponseEntity.badRequest().body("Error: masteryLevel must be between 1 and 10!");
        }

        return userSkillService.createUserSkill(request)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body("Error: User or skill not found!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserSkill(@PathVariable UUID id, @RequestBody UserSkillRequest request) {
        if (!validMastery(request.getMasteryLevel())) {
            return ResponseEntity.badRequest().body("Error: masteryLevel must be between 1 and 10!");
        }

        return userSkillService.updateUserSkill(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserSkill(@PathVariable UUID id) {
        if (userSkillService.deleteUserSkill(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private boolean validMastery(Integer value) {
        return value == null || (value >= 1 && value <= 10);
    }
}
