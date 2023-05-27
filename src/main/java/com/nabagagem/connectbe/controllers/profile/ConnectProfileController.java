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
import com.nabagagem.connectbe.services.ProfileService;
import com.nabagagem.connectbe.services.SlugService;
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

import java.time.DayOfWeek;
import java.util.Map;
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
    public ProfilePayload get(@PathVariable String id) {
        return profileService.getProfile(slugService.getProfileIdFrom(id));
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