package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.exceptions.BadRequestException;
import com.nabagagem.connectbe.domain.exceptions.ErrorType;
import com.nabagagem.connectbe.domain.exceptions.SkillTopCountExceeded;
import com.nabagagem.connectbe.domain.profile.AvailabilityCommand;
import com.nabagagem.connectbe.domain.profile.AvailabilityType;
import com.nabagagem.connectbe.domain.profile.BioCommand;
import com.nabagagem.connectbe.domain.profile.CertificationsCommand;
import com.nabagagem.connectbe.domain.profile.PatchSkillCommand;
import com.nabagagem.connectbe.domain.profile.PersonalInfoCommand;
import com.nabagagem.connectbe.domain.profile.SkillCommand;
import com.nabagagem.connectbe.domain.profile.SkillPayload;
import com.nabagagem.connectbe.domain.profile.SkillReadPayload;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;
import com.nabagagem.connectbe.entities.ProfileType;
import com.nabagagem.connectbe.repos.AvailabilityRepo;
import com.nabagagem.connectbe.repos.CertificationRepo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.repos.ProfileSkillRepo;
import com.nabagagem.connectbe.services.notifications.PublishNotification;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedReturnValue")
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
    private final ProfileInitService profileInitService;
    private final ProfileIndexingService profileIndexingService;

    @PublishNotification
    public ConnectProfile updateInfo(@Valid PersonalInfoCommand personalInfoCommand) {
        ConnectProfile profile = findOrInit(personalInfoCommand.id());
        PersonalInfo personalInfo = personalInfoCommand.personalInfo();
        personalInfo.setSlug(formatSlug(personalInfo.getSlug()));
        personalInfo.setReady(true);
        profile.setPersonalInfo(personalInfo);
        return save(profile);
    }

    private String formatSlug(String slug) {
        return slug.toLowerCase().trim().replaceAll("\\s", "-");
    }

    ConnectProfile findOrInit(UUID id) {
        return profileRepo.findById(id)
                .orElseGet(() -> ConnectProfile.builder().id(id).build());
    }

    public PersonalInfo getInfo(UUID id) {
        return profileRepo.findForProfileRead(id)
                .map(ConnectProfile::getPersonalInfo)
                .orElseGet(() -> init(id).getPersonalInfo());
    }

    ConnectProfile init(UUID id) {
        return save(profileInitService.initFromAuth(id));
    }

    @PublishNotification
    public ConnectProfile updateSkills(SkillCommand skillCommand) {
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
        return save(profile);
    }

    public Set<SkillReadPayload> getSkills(UUID id) {
        return profileSkillRepo.findByProfileId(id)
                .stream()
                .map(profileMapper::toSkillReadPayload)
                .collect(Collectors.toSet());
    }

    @PublishNotification
    public ConnectProfile updateCertifications(CertificationsCommand certificationsCommand) {
        UUID id = certificationsCommand.id();
        certificationRepo.deleteByProfileId(id);
        ConnectProfile profile = findOrInit(id);
        profile.setCertifications(certificationsCommand.certifications()
                .stream().map(certificationPayload -> profileMapper.toCertification(
                        certificationPayload, profile))
                .collect(Collectors.toSet()));
        return save(profile);
    }

    public Set<CertificationPayload> getCertifications(UUID id) {
        return certificationRepo.findByProfileId(id)
                .stream().map(profileMapper::toCertPayload)
                .collect(Collectors.toSet());
    }

    public ConnectProfile save(ConnectProfile profile) {
        profile.setKeywords(profileIndexingService.extractFrom(profile));
        profile.setProfileType(
                Optional.ofNullable(profile.getProfileType())
                        .orElse(ProfileType.USER)
        );
        return profileRepo.save(profile);
    }

    @PublishNotification
    public ConnectProfile updateAvailability(AvailabilityCommand availabilityCommand) {
        UUID id = availabilityCommand.id();
        availabilityRepo.deleteByProfileId(id);
        ConnectProfile profile = findOrInit(id);
        profile.setAvailabilities(profileMapper.mapAvailabilities(
                availabilityCommand.availabilities(), profile));
        return save(profile);
    }

    public Map<DayOfWeek, AvailabilityType> getAvailabilities(UUID id) {
        return profileMapper.toAvailPayload(
                availabilityRepo.findByProfileId(id)
        );
    }

    @PublishNotification
    public ConnectProfile updateBio(BioCommand bioCommand) {
        ConnectProfile profile = findOrInit(bioCommand.id());
        profile.setProfileBio(bioCommand.profileBio());
        return save(profile);
    }

    public Optional<ProfileBio> getProfileBio(UUID id) {
        return profileRepo.findForProfileRead(id)
                .map(ConnectProfile::getProfileBio);
    }

    @PublishNotification
    public ConnectProfile patchSkill(PatchSkillCommand patchSkillCommand) {
        ConnectProfile profile = profileRepo.findById(patchSkillCommand.id()).orElseThrow();
        return updateSkills(new SkillCommand(
                patchSkillCommand.id(),
                profile.getProfileSkills()
                        .stream().peek(skill -> {
                            if (skill.getId().equals(patchSkillCommand.skillId())) {
                                skill.setTop(patchSkillCommand.patchSkillPayload().top());
                            }
                        }).map(profileMapper::toSkillPayload)
                        .collect(Collectors.toSet())
        ));
    }

    public ConnectProfile findOrFail(UUID id) {
        return profileRepo.findById(id).orElseThrow();
    }

    public void failIfEmailExists(String email) {
        if (profileRepo.existsByPersonalInfoEmail(email)) {
            throw BadRequestException.builder()
                    .errorType(ErrorType.EMAIL_ALREADY_EXISTS)
                    .build();
        }
    }

    public ConnectProfile findOrCreate(UUID profileId) {
        return profileRepo.findById(profileId)
                .orElseGet(() -> init(profileId));
    }
}
