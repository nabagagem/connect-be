package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.AvailabilityCommand;
import com.nabagagem.connectbe.domain.AvailabilityType;
import com.nabagagem.connectbe.domain.BioCommand;
import com.nabagagem.connectbe.domain.CertificationsCommand;
import com.nabagagem.connectbe.domain.PatchSkillCommand;
import com.nabagagem.connectbe.domain.PatchSkillPayload;
import com.nabagagem.connectbe.domain.PersonalInfoCommand;
import com.nabagagem.connectbe.domain.ProfilePayload;
import com.nabagagem.connectbe.domain.SkillCommand;
import com.nabagagem.connectbe.domain.SkillPayload;
import com.nabagagem.connectbe.domain.SkillReadPayload;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;
import com.nabagagem.connectbe.services.profile.ProfileAuthService;
import com.nabagagem.connectbe.services.profile.ProfileService;
import com.nabagagem.connectbe.services.profile.SlugService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.DayOfWeek;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{id}")
public class ConnectProfileController {
    private final ProfileService profileService;
    private final SlugService slugService;
    private final ProfileAuthService profileAuthService;

    @GetMapping
    public ProfilePayload get(@PathVariable String id,
                              Principal principal) {
        return profileAuthService.isAllowedOn(
                profileService.getProfile(
                        slugService.getProfileIdFrom(id),
                        getUserIdOrNull(principal))
        );
    }

    private UUID getUserIdOrNull(Principal principal) {
        try {
            return Optional.ofNullable(principal)
                    .map(Principal::getName)
                    .map(UUID::fromString)
                    .orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @PutMapping("/info")
    public void updatePersonalInfo(@PathVariable String id,
                                   @RequestBody @Valid PersonalInfo personalInfo) {
        UUID profileId = getAndValidateOwnership(id);
        profileService.updateInfo(new PersonalInfoCommand(
                profileId,
                personalInfo));
    }

    @GetMapping("/info")
    public PersonalInfo getInfo(@PathVariable String id) {
        UUID profileId = getAndValidateOwnership(id);
        return profileService.getInfo(profileId);
    }

    @PutMapping("/skills")
    public void updateSkills(@PathVariable String id,
                             @RequestBody Set<@Valid SkillPayload> skills) {
        UUID profileId = getAndValidateOwnership(id);
        profileService.updateSkills(new SkillCommand(
                profileId,
                skills));
    }

    @PatchMapping("/skills/{skillId}")
    public void patchSkill(@PathVariable String id,
                           @PathVariable UUID skillId,
                           @RequestBody @Valid PatchSkillPayload patchSkillPayload) {
        UUID profileId = getAndValidateOwnership(id);
        profileService.patchSkill(new PatchSkillCommand(
                profileId,
                skillId, patchSkillPayload));
    }

    @GetMapping("/skills")
    public Set<SkillReadPayload> getSkills(@PathVariable String id) {
        UUID profileId = getAndValidateOwnership(id);
        return profileService.getSkills(
                profileId
        );
    }

    @PutMapping("/certifications")
    public void updateCertifications(@PathVariable String id,
                                     @RequestBody Set<@Valid CertificationPayload> certifications) {
        UUID profileId = getAndValidateOwnership(id);
        profileService.updateCertifications(new CertificationsCommand(
                profileId,
                certifications));
    }

    @GetMapping("/certifications")
    public Set<CertificationPayload> getCertifications(@PathVariable String id) {
        UUID profileId = getAndValidateOwnership(id);
        return profileService.getCertifications(
                profileId
        );
    }

    @PutMapping("/availability")
    public void updateAvailability(@PathVariable String id,
                                   @RequestBody Map<DayOfWeek, AvailabilityType> availabilities) {
        UUID profileId = getAndValidateOwnership(id);
        profileService.updateAvailability(
                new AvailabilityCommand(
                        profileId,
                        availabilities));
    }

    @GetMapping("/availability")
    public Map<DayOfWeek, AvailabilityType> getAvailability(@PathVariable String id) {
        UUID profileId = getAndValidateOwnership(id);
        return profileService.getAvailabilities(
                profileId
        );
    }

    @PutMapping("/bio")
    public void updateBio(@PathVariable String id,
                          @RequestBody @Valid ProfileBio profileBio) {
        UUID profileId = getAndValidateOwnership(id);
        profileService.updateBio(new BioCommand(
                profileId,
                profileBio));
    }

    @GetMapping("/bio")
    public ResponseEntity<ProfileBio> getBio(@PathVariable String id) {
        UUID profileId = getAndValidateOwnership(id);
        return profileService.getProfileBio(profileId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private UUID getAndValidateOwnership(String id) {
        UUID profileId = slugService.getProfileIdFrom(id);
        profileAuthService.failIfNotLoggedIn(profileId);
        return profileId;
    }
}