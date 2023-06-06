package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record ProfilePayload(
        UUID id,
        PersonalInfo personalInfo,
        Double averageStars,
        Set<SkillReadPayload> skills,
        Set<CertificationPayload> certifications,
        ProfileMetrics profileMetrics,
        ProfileBio bio,
        Map<DayOfWeek, AvailabilityType> availabilities,
        ProfileRatingPayload myRating, List<ProfileRatingPayload> lastRatings) {
}
