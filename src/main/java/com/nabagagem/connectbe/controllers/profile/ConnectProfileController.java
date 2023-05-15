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
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{id}")
@PostAuthorize("#id == authentication.name")
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

    @GetMapping("/skills")
    public Set<SkillPayload> getSkills(@PathVariable String id) {
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
