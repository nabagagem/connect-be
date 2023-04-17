package com.nabagagem.connectbe.controllers;

import com.nabagagem.connectbe.domain.CertificationsCommand;
import com.nabagagem.connectbe.domain.PersonalInfoCommand;
import com.nabagagem.connectbe.domain.SkillCommand;
import com.nabagagem.connectbe.domain.SkillPayload;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.services.ProfileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{id}")
public class ConnectProfileController {
    private final ProfileService profileService;

    @PutMapping("/info")
    public void updatePersonalInfo(@PathVariable String id,
                                   @RequestBody @Valid PersonalInfo personalInfo) {
        profileService.updateInfo(new PersonalInfoCommand(id, personalInfo));
    }

    @GetMapping("/info")
    public PersonalInfo getInfo(@PathVariable String id) {
        return profileService.getInfo(id)
                .orElseGet(PersonalInfo::new);
    }

    @PutMapping("/skills")
    public void updateSkills(@PathVariable String id,
                             @RequestBody Set<@Valid SkillPayload> skills) {
        profileService.updateSkills(new SkillCommand(id, skills));
    }
    
    @GetMapping("/skills")
    public Set<SkillPayload> getSkills(@PathVariable String id) {
        return profileService.getSkills(id);
    }

    @PutMapping("/certifications")
    public void updateCertifications(@PathVariable String id,
                                     @RequestBody Set<@Valid CertificationPayload> certifications) {
        profileService.updateCertifications(new CertificationsCommand(id, certifications));
    }

    @GetMapping("/certifications")
    public Set<CertificationPayload> getCertifications(@PathVariable String id) {
        return profileService.getCertifications(id);
    }
}
