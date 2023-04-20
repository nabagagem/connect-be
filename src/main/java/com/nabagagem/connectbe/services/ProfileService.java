package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.AvailabilityCommand;
import com.nabagagem.connectbe.domain.AvailabilityType;
import com.nabagagem.connectbe.domain.BioCommand;
import com.nabagagem.connectbe.domain.CertificationsCommand;
import com.nabagagem.connectbe.domain.PersonalInfoCommand;
import com.nabagagem.connectbe.domain.ProfilePayload;
import com.nabagagem.connectbe.domain.SkillCommand;
import com.nabagagem.connectbe.domain.SkillPayload;
import com.nabagagem.connectbe.domain.exceptions.SkillTopCountExceeded;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;
import com.nabagagem.connectbe.resources.AvailabilityRepo;
import com.nabagagem.connectbe.resources.CertificationRepo;
import com.nabagagem.connectbe.resources.ProfileRepo;
import com.nabagagem.connectbe.resources.ProfileSkillRepo;
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

    public PersonalInfo getInfo(String id) {
        return profileRepo.findById(UUID.fromString(id))
                .map(ConnectProfile::getPersonalInfo)
                .orElseGet(() -> authService.initFromAuth(UUID.fromString(id)).getPersonalInfo());
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

    public ProfilePayload getProfile(String idStr) {
        UUID id = UUID.fromString(idStr);
        ConnectProfile profile = profileRepo.findById(id)
                .orElseGet(() -> authService.initFromAuth(id));
        return new ProfilePayload(
                profile.getPersonalInfo(),
                profileMapper.toSkillsPayload(
                        Optional.ofNullable(profile.getProfileSkills())
                                .orElseGet(Collections::emptySet)
                ),
                profileMapper.toCertsPayload(
                        Optional.ofNullable(profile.getCertifications())
                                .orElseGet(Collections::emptySet)
                )
        );
    }

    public void updateAvailability(AvailabilityCommand availabilityCommand) {
        UUID id = UUID.fromString(availabilityCommand.id());
        availabilityRepo.deleteByProfileId(id);
        ConnectProfile profile = findOrInit(id);
        profile.setAvailabilities(profileMapper.mapAvailabilities(
                availabilityCommand.availabilities(), profile));
        save(profile);
    }

    public Map<DayOfWeek, Set<AvailabilityType>> getAvailabilities(String id) {
        return profileMapper.toAvailPayload(
                availabilityRepo.findByProfileId(UUID.fromString(id))
        );
    }

    public void updateBio(BioCommand bioCommand) {
        ConnectProfile profile = findOrInit(UUID.fromString(bioCommand.id()));
        profile.setProfileBio(bioCommand.profileBio());
        save(profile);
    }

    public Optional<ProfileBio> getProfileBio(String id) {
        return profileRepo.findById(UUID.fromString(id))
                .map(ConnectProfile::getProfileBio);
    }
}
