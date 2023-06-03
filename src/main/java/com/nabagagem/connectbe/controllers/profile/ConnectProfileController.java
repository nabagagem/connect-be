package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.*;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;
import com.nabagagem.connectbe.services.ProfileService;
import com.nabagagem.connectbe.services.SlugService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ProfilePayload get(@PathVariable String id,
                              Principal principal) {
        return profileService.getProfile(
                slugService.getProfileIdFrom(id),
                getUserIdOrNull(principal));
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
        profileService.updateInfo(new PersonalInfoCommand(
                slugService.getProfileIdFrom(id),
                personalInfo));
    }

    @GetMapping("/info")
    public PersonalInfo getInfo(@PathVariable String id) {
        return profileService.getInfo(slugService.getProfileIdFrom(id));
    }

    @PutMapping("/skills")
    public void updateSkills(@PathVariable String id,
                             @RequestBody Set<@Valid SkillPayload> skills) {
        profileService.updateSkills(new SkillCommand(
                slugService.getProfileIdFrom(id),
                skills));
    }

    @PatchMapping("/skills/{skillId}")
    public void patchSkill(@PathVariable String id,
                           @PathVariable UUID skillId,
                           @RequestBody @Valid PatchSkillPayload patchSkillPayload) {
        profileService.patchSkill(new PatchSkillCommand(
                slugService.getProfileIdFrom(id),
                skillId, patchSkillPayload));
    }

    @GetMapping("/skills")
    public Set<SkillReadPayload> getSkills(@PathVariable String id) {
        return profileService.getSkills(
                slugService.getProfileIdFrom(id)
        );
    }

    @PutMapping("/certifications")
    public void updateCertifications(@PathVariable String id,
                                     @RequestBody Set<@Valid CertificationPayload> certifications) {
        profileService.updateCertifications(new CertificationsCommand(
                slugService.getProfileIdFrom(id),
                certifications));
    }

    @GetMapping("/certifications")
    public Set<CertificationPayload> getCertifications(@PathVariable String id) {
        return profileService.getCertifications(
                slugService.getProfileIdFrom(id)
        );
    }

    @PutMapping("/availability")
    public void updateAvailability(@PathVariable String id,
                                   @RequestBody Map<DayOfWeek, Set<AvailabilityType>> availabilities) {
        profileService.updateAvailability(
                new AvailabilityCommand(
                        slugService.getProfileIdFrom(id),
                        availabilities));
    }

    @GetMapping("/availability")
    public Map<DayOfWeek, Set<AvailabilityType>> getAvailability(@PathVariable String id) {
        return profileService.getAvailabilities(
                slugService.getProfileIdFrom(id)
        );
    }

    @PutMapping("/bio")
    public void updateBio(@PathVariable String id,
                          @RequestBody @Valid ProfileBio profileBio) {
        profileService.updateBio(new BioCommand(
                slugService.getProfileIdFrom(id),
                profileBio));
    }

    @GetMapping("/bio")
    public ResponseEntity<ProfileBio> getBio(@PathVariable String id) {
        return profileService.getProfileBio(slugService.getProfileIdFrom(id))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}