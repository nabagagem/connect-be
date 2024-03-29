package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.AvailabilityType;
import com.nabagagem.connectbe.domain.profile.SkillPayload;
import com.nabagagem.connectbe.domain.profile.SkillReadPayload;
import com.nabagagem.connectbe.entities.Availability;
import com.nabagagem.connectbe.entities.Certification;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.LinkType;
import com.nabagagem.connectbe.entities.ProfileLink;
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

    public Set<CertificationPayload> toCertsPayload(Set<Certification> certifications) {
        return certifications.stream().map(this::toCertPayload)
                .collect(Collectors.toSet());
    }

    public Set<Availability> mapAvailabilities(Map<DayOfWeek, AvailabilityType> availabilities,
                                               ConnectProfile profile) {
        return availabilities.entrySet()
                .stream().map(entry -> Availability.builder()
                        .profile(profile)
                        .availabilityType(entry.getValue())
                        .dayOfWeek(entry.getKey())
                        .id(UUID.randomUUID())
                        .build()).collect(Collectors.toSet());
    }

    public Map<DayOfWeek, AvailabilityType> toAvailPayload(Set<Availability> availabilities) {
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

    public Map<LinkType, String> toLinksMap(Set<ProfileLink> profileLinks) {
        return Optional.ofNullable(profileLinks)
                .map(__ -> profileLinks.stream().collect(
                        Collectors.toMap(
                                ProfileLink::getLinkType,
                                ProfileLink::getLinkURL
                        )
                )).orElseGet(Collections::emptyMap);
    }
}
