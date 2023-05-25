package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.AvailabilityCommand;
import com.nabagagem.connectbe.domain.AvailabilityType;
import com.nabagagem.connectbe.domain.BioCommand;
import com.nabagagem.connectbe.domain.CertificationsCommand;
import com.nabagagem.connectbe.domain.PatchSkillCommand;
import com.nabagagem.connectbe.domain.PersonalInfoCommand;
import com.nabagagem.connectbe.domain.ProfilePayload;
import com.nabagagem.connectbe.domain.SkillCommand;
import com.nabagagem.connectbe.domain.SkillPayload;
import com.nabagagem.connectbe.domain.SkillReadPayload;
import com.nabagagem.connectbe.domain.exceptions.SkillTopCountExceeded;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;
import com.nabagagem.connectbe.repos.AvailabilityRepo;
import com.nabagagem.connectbe.repos.CertificationRepo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.repos.ProfileSkillRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.Map;
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
    private final AvailabilityRepo availabilityRepo;
    private final AuthService authService;
    private final ProfileMetricsService profileMetricsService;

    public void updateInfo(@Valid PersonalInfoCommand personalInfoCommand) {
        ConnectProfile profile = findOrInit(personalInfoCommand.id());
        profile.setPersonalInfo(personalInfoCommand.personalInfo());
        save(profile);
    }

    ConnectProfile findOrInit(UUID id) {
        return profileRepo.findById(id)
                .orElseGet(() -> ConnectProfile.builder().id(id).build());
    }

    public PersonalInfo getInfo(UUID id) {
        return profileRepo.findById(id)
                .map(ConnectProfile::getPersonalInfo)
                .orElseGet(() -> authService.initFromAuth(id).getPersonalInfo());
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
        profileSkillRepo.deleteByProfileId(skillCommand.id());
        ConnectProfile profile = findOrInit(skillCommand.id());
        profile.setProfileSkills(skillCommand.skills()
                .stream().map(skill -> profileMapper.toProfileSkill(skill, profile))
                .collect(Collectors.toSet()));
        save(profile);
    }

    public Set<SkillReadPayload> getSkills(UUID id) {
        return profileSkillRepo.findByProfileId(id)
                .stream()
                .map(profileMapper::toSkillReadPayload)
                .collect(Collectors.toSet());
    }

    public void updateCertifications(CertificationsCommand certificationsCommand) {
        UUID id = certificationsCommand.id();
        certificationRepo.deleteByProfileId(id);
        ConnectProfile profile = findOrInit(id);
        profile.setCertifications(certificationsCommand.certifications()
                .stream().map(certificationPayload -> profileMapper.toCertification(
                        certificationPayload, profile))
                .collect(Collectors.toSet()));
        save(profile);
    }

    public Set<CertificationPayload> getCertifications(UUID id) {
        return certificationRepo.findByProfileId(id)
                .stream().map(profileMapper::toCertPayload)
                .collect(Collectors.toSet());
    }

    void save(ConnectProfile profile) {
        profileRepo.save(profile);
    }

    public ProfilePayload getProfile(UUID id) {
        ConnectProfile profile = profileRepo.findById(id)
                .orElseGet(() -> authService.initFromAuth(id));
        return new ProfilePayload(
                profile.getPersonalInfo(),
                Optional.ofNullable(profile.getProfileSkills())
                        .map(profileSkills -> profileSkills
                                .stream().map(profileMapper::toSkillReadPayload)
                                .collect(Collectors.toSet())).orElseGet(Collections::emptySet),
                profileMapper.toCertsPayload(
                        Optional.ofNullable(profile.getCertifications())
                                .orElseGet(Collections::emptySet)
                ),
                profileMetricsService.getMetricsFor(id).orElse(null),
                profile.getProfileBio(),
                profileMapper.toAvailPayload(profile.getAvailabilities())
        );
    }

    public void updateAvailability(AvailabilityCommand availabilityCommand) {
        UUID id = availabilityCommand.id();
        availabilityRepo.deleteByProfileId(id);
        ConnectProfile profile = findOrInit(id);
        profile.setAvailabilities(profileMapper.mapAvailabilities(
                availabilityCommand.availabilities(), profile));
        save(profile);
    }

    public Map<DayOfWeek, Set<AvailabilityType>> getAvailabilities(UUID id) {
        return profileMapper.toAvailPayload(
                availabilityRepo.findByProfileId(id)
        );
    }

    public void updateBio(BioCommand bioCommand) {
        ConnectProfile profile = findOrInit(bioCommand.id());
        profile.setProfileBio(bioCommand.profileBio());
        save(profile);
    }

    public Optional<ProfileBio> getProfileBio(UUID id) {
        return profileRepo.findById(id)
                .map(ConnectProfile::getProfileBio);
    }

    public void patchSkill(PatchSkillCommand patchSkillCommand) {
        profileRepo.findById(patchSkillCommand.id())
                .ifPresent(profile -> {
                    updateSkills(new SkillCommand(
                            patchSkillCommand.id(),
                            profile.getProfileSkills()
                                    .stream().peek(skill -> {
                                        if (skill.getId().equals(patchSkillCommand.skillId())) {
                                            skill.setTop(patchSkillCommand.patchSkillPayload().top());
                                        }
                                    }).map(profileMapper::toSkillPayload)
                                    .collect(Collectors.toSet())
                    ));
                });
    }
}
