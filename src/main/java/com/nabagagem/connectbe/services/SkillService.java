package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.resources.SkillRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class SkillService {
    private final SkillRepo skillRepo;

    public Skill findOrCreate(String skillName) {
        return skillRepo.findByName(skillName)
                .orElseGet(() -> skillRepo.save(
                        Skill.builder()
                                .name(skillName)
                                .build()
                ));
    }
}
