package com.nabagagem.connectbe.domain.profile;

import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;
import com.nabagagem.connectbe.entities.ProfileType;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record ProfilePayload(
        UUID id,
        UUID parentId,
        PersonalInfo personalInfo,
        Set<SkillReadPayload> skills,
        Set<CertificationPayload> certifications,
        ProfileBio bio,
        Map<DayOfWeek, AvailabilityType> availabilities,
        ProfileType profileType,
        Set<ProfilePayload> altProfiles) {
}
