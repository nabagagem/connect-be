package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;

public record ProfilePayload(
        PersonalInfo personalInfo,
        Set<SkillReadPayload> skills,
        Set<CertificationPayload> certifications,
        ProfileMetrics profileMetrics,
        ProfileBio bio,
        Map<DayOfWeek, Set<AvailabilityType>> availabilities
) {
}
