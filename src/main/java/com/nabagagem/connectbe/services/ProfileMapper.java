package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.AvailabilityType;
import com.nabagagem.connectbe.domain.SkillPayload;
import com.nabagagem.connectbe.domain.SkillReadPayload;
import com.nabagagem.connectbe.entities.Availability;
import com.nabagagem.connectbe.entities.Certification;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.ProfileSkill;
import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.repos.SkillRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ProfileMapper {
    private final SkillRepo skillRepo;

    public ProfileSkill toProfileSkill(SkillPayload skill,
                                       ConnectProfile profile) {
        return ProfileSkill.builder()
                .id(UUID.randomUUID())
                .skill(findOrCreate(skill.name()))
                .certifications(skill.certifications())
                .level(skill.level())
                .profile(profile)
                .top(skill.top())
                .build();
    }

    private Skill findOrCreate(String name) {
        return skillRepo.findByName(name)
                .orElseGet(() -> skillRepo.save(
                        Skill.builder()
                                .name(name)
                                .build()));
    }

    public SkillPayload toSkillPayload(ProfileSkill profileSkill) {
        return new SkillPayload(
                profileSkill.getSkill().getName(),
                profileSkill.getCertifications(),
                profileSkill.getLevel(),
                profileSkill.getTop()
        );
    }

    public Certification toCertification(CertificationPayload certificationPayload,
                                         ConnectProfile profile) {
        return Certification.builder()
                .id(UUID.randomUUID())
                .title(certificationPayload.title())
                .year(certificationPayload.year())
                .profile(profile)
                .build();
    }

    public CertificationPayload toCertPayload(Certification certification) {
        return new CertificationPayload(
                certification.getTitle(),
                certification.getYear()
        );
    }

    public Set<SkillPayload> toSkillsPayload(Set<ProfileSkill> profileSkills) {
        return profileSkills.stream().map(this::toSkillPayload)
                .collect(Collectors.toSet());
    }

    public Set<CertificationPayload> toCertsPayload(Set<Certification> certifications) {
        return certifications.stream().map(this::toCertPayload)
                .collect(Collectors.toSet());
    }

    public Set<Availability> mapAvailabilities(Map<DayOfWeek, Set<AvailabilityType>> availabilities,
                                               ConnectProfile profile) {
        return availabilities.entrySet()
                .stream().map(entry -> Availability.builder()
                        .profile(profile)
                        .availabilityType(entry.getValue())
                        .dayOfWeek(entry.getKey())
                        .id(UUID.randomUUID())
                        .build()).collect(Collectors.toSet());
    }

    public Map<DayOfWeek, Set<AvailabilityType>> toAvailPayload(Set<Availability> availabilities) {
        return Optional.ofNullable(availabilities)
                .map(__ -> availabilities
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Availability::getDayOfWeek,
                                        Availability::getAvailabilityType
                                )
                        )).orElseGet(Collections::emptyMap);
    }

    public SkillReadPayload toSkillReadPayload(ProfileSkill profileSkill) {
        return new SkillReadPayload(
                profileSkill.getId(),
                toSkillPayload(profileSkill)
        );
    }
}
