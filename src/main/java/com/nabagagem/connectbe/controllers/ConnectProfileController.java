package com.nabagagem.connectbe.controllers;

import com.nabagagem.connectbe.domain.PersonalInfoCommand;
import com.nabagagem.connectbe.domain.SkillCommand;
import com.nabagagem.connectbe.domain.SkillPayload;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.services.ProfileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                             @RequestBody @NotEmpty Set<@Valid SkillPayload> skills) {
        profileService.updateSkills(new SkillCommand(id, skills));
    }


    @GetMapping("/skills")
    public Set<SkillPayload> getSkills(@PathVariable String id) {
        return profileService.getSkills(id);
    }
}