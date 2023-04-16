package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.PersonalInfoCommand;
import com.nabagagem.connectbe.domain.SkillCommand;
import com.nabagagem.connectbe.domain.SkillPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileSkill;
import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.resources.ProfileRepo;
import com.nabagagem.connectbe.resources.ProfileSkillRepo;
import com.nabagagem.connectbe.resources.SkillRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
@AllArgsConstructor
public class ProfileService {
    private final ProfileRepo profileRepo;
    private final SkillRepo skillRepo;
    private final ProfileSkillRepo profileSkillRepo;

    @Transactional
    public void updateInfo(@Valid PersonalInfoCommand personalInfoCommand) {
        ConnectProfile profile = findOrInit(personalInfoCommand.id());
        profile.setPersonalInfo(personalInfoCommand.personalInfo());
        profileRepo.save(profile);
    }

    private ConnectProfile findOrInit(String strId) {
        UUID id = UUID.fromString(strId);
        ConnectProfile profile = profileRepo.findById(id)
                .orElseGet(ConnectProfile::new);
        profile.setId(id);
        return profile;
    }

    public Optional<PersonalInfo> getInfo(String id) {
        return profileRepo.findById(UUID.fromString(id))
                .map(ConnectProfile::getPersonalInfo);
    }

    public void updateSkills(SkillCommand skillCommand) {
        ConnectProfile profile = findOrInit(skillCommand.id());
        profile.setProfileSkills(skillCommand.skills()
                .stream().map(skill -> ProfileSkill.builder()
                        .skill(findOrCreate(skill.name()))
                        .certifications(skill.certifications())
                        .level(skill.level())
                        .profile(profile)
                        .build())
                .collect(Collectors.toSet()));
        profileRepo.save(profile);
    }

    private Skill findOrCreate(String name) {
        return skillRepo.findByName(name)
                .orElseGet(() -> skillRepo.save(
                        Skill.builder()
                                .name(name)
                                .build()));
    }

    public Set<SkillPayload> getSkills(String id) {
        return profileSkillRepo.findByProfileId(UUID.fromString(id))
                .stream()
                .map(profileSkill -> new SkillPayload(
                        profileSkill.getSkill().getName(),
                        profileSkill.getCertifications(),
                        profileSkill.getLevel()
                )).collect(Collectors.toSet());

    }
}
