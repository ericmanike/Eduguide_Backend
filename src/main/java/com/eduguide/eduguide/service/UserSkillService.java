package com.eduguide.eduguide.service;

import com.eduguide.eduguide.model.UserSkill;
import com.eduguide.eduguide.model.UserSkillRequest;
import com.eduguide.eduguide.repository.SkillRepository;
import com.eduguide.eduguide.repository.UserRepository;
import com.eduguide.eduguide.repository.UserSkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public UserSkillService(
            UserSkillRepository userSkillRepository,
            UserRepository userRepository,
            SkillRepository skillRepository
    ) {
        this.userSkillRepository = userSkillRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    public List<UserSkill> getAllUserSkills() {
        return userSkillRepository.findAll();
    }

    public List<UserSkill> getUserSkillsByUserId(UUID userId) {
        return userSkillRepository.findByUserId(userId);
    }

    public Optional<UserSkill> createUserSkill(UserSkillRequest request) {
        var userOptional = userRepository.findById(request.getUserId());
        var skillOptional = skillRepository.findById(request.getSkillId());

        if (userOptional.isPresent() && skillOptional.isPresent()) {
            UserSkill userSkill = new UserSkill();
            userSkill.setUser(userOptional.get());
            userSkill.setSkill(skillOptional.get());
            userSkill.setMasteryLevel(request.getMasteryLevel());
            return Optional.of(userSkillRepository.save(userSkill));
        }
        return Optional.empty();
    }

    public Optional<UserSkill> updateUserSkill(UUID id, UserSkillRequest request) {
        return userSkillRepository.findById(id).map(userSkill -> {
            if (request.getMasteryLevel() != null) {
                userSkill.setMasteryLevel(request.getMasteryLevel());
            }
            return userSkillRepository.save(userSkill);
        });
    }

    public boolean deleteUserSkill(UUID id) {
        if (userSkillRepository.existsById(id)) {
            userSkillRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
