package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.CertificationsCommand;
import com.nabagagem.connectbe.domain.PersonalInfoCommand;
import com.nabagagem.connectbe.domain.SkillCommand;
import com.nabagagem.connectbe.domain.SkillPayload;
import com.nabagagem.connectbe.domain.exceptions.SkillTopCountExceeded;
import com.nabagagem.connectbe.entities.*;
import com.nabagagem.connectbe.resources.CertificationRepo;
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
@Transactional
public class ProfileService {
    private final ProfileRepo profileRepo;
    private final SkillRepo skillRepo;
    private final ProfileSkillRepo profileSkillRepo;
    private final CertificationRepo certificationRepo;

    public void updateInfo(@Valid PersonalInfoCommand personalInfoCommand) {
        ConnectProfile profile = findOrInit(UUID.fromString(personalInfoCommand.id()));
        profile.setPersonalInfo(personalInfoCommand.personalInfo());
        profileRepo.save(profile);
    }

    private ConnectProfile findOrInit(UUID id) {
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
        long topCount = skillCommand.skills()
                .stream()
                .map(SkillPayload::top)
                .filter(Boolean::booleanValue)
                .count();
        if (topCount > 3) {
            throw new SkillTopCountExceeded();
        }
        UUID profileId = UUID.fromString(skillCommand.id());
        profileSkillRepo.deleteByProfileId(profileId);
        ConnectProfile profile = findOrInit(profileId);
        profile.setProfileSkills(skillCommand.skills()
                .stream().map(skill -> ProfileSkill.builder()
                        .id(UUID.randomUUID())
                        .skill(findOrCreate(skill.name()))
                        .certifications(skill.certifications())
                        .level(skill.level())
                        .profile(profile)
                        .top(skill.top())
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
                        profileSkill.getLevel(),
                        profileSkill.getTop()
                )).collect(Collectors.toSet());

    }

    public void updateCertifications(CertificationsCommand certificationsCommand) {
        UUID id = UUID.fromString(certificationsCommand.id());
        certificationRepo.deleteByProfileId(id);
        ConnectProfile profile = findOrInit(id);
        profile.setCertifications(certificationsCommand.certifications()
                .stream().map(certificationPayload -> Certification.builder()
                        .id(UUID.randomUUID())
                        .title(certificationPayload.title())
                        .year(certificationPayload.year())
                        .profile(profile)
                        .build())
                .collect(Collectors.toSet()));
        profileRepo.save(profile);
    }

    public Set<CertificationPayload> getCertifications(String id) {
        return certificationRepo.findByProfileId(UUID.fromString(id))
                .stream().map(certification -> new CertificationPayload(
                        certification.getTitle(),
                        certification.getYear()
                )).collect(Collectors.toSet());
    }
}
