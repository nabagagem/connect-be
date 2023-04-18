package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.CertificationsCommand;
import com.nabagagem.connectbe.domain.PersonalInfoCommand;
import com.nabagagem.connectbe.domain.ProfilePayload;
import com.nabagagem.connectbe.domain.SkillCommand;
import com.nabagagem.connectbe.domain.SkillPayload;
import com.nabagagem.connectbe.domain.exceptions.SkillTopCountExceeded;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.resources.CertificationRepo;
import com.nabagagem.connectbe.resources.ProfileRepo;
import com.nabagagem.connectbe.resources.ProfileSkillRepo;
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
    private final ProfileSkillRepo profileSkillRepo;
    private final CertificationRepo certificationRepo;
    private final ProfileMapper profileMapper;

    public void updateInfo(@Valid PersonalInfoCommand personalInfoCommand) {
        ConnectProfile profile = findOrInit(UUID.fromString(personalInfoCommand.id()));
        profile.setPersonalInfo(personalInfoCommand.personalInfo());
        save(profile);
    }

    ConnectProfile findOrInit(UUID id) {
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
                .stream().map(skill -> profileMapper.toProfileSkill(skill, profile))
                .collect(Collectors.toSet()));
        save(profile);
    }

    public Set<SkillPayload> getSkills(String id) {
        return profileSkillRepo.findByProfileId(UUID.fromString(id))
                .stream()
                .map(profileMapper::toSkillPayload)
                .collect(Collectors.toSet());

    }

    public void updateCertifications(CertificationsCommand certificationsCommand) {
        UUID id = UUID.fromString(certificationsCommand.id());
        certificationRepo.deleteByProfileId(id);
        ConnectProfile profile = findOrInit(id);
        profile.setCertifications(certificationsCommand.certifications()
                .stream().map(certificationPayload -> profileMapper.toCertification(
                        certificationPayload, profile))
                .collect(Collectors.toSet()));
        save(profile);
    }

    public Set<CertificationPayload> getCertifications(String id) {
        return certificationRepo.findByProfileId(UUID.fromString(id))
                .stream().map(profileMapper::toCertPayload)
                .collect(Collectors.toSet());
    }

    void save(ConnectProfile profile) {
        profileRepo.save(profile);
    }

    public Optional<ProfilePayload> getProfile(String id) {
        return profileRepo.findById(UUID.fromString(id))
                .map(connectProfile -> new ProfilePayload(
                        connectProfile.getPersonalInfo(),
                        profileMapper.toSkillsPayload(connectProfile.getProfileSkills()),
                        profileMapper.toCertsPayload(connectProfile.getCertifications())
                ));
    }
}
