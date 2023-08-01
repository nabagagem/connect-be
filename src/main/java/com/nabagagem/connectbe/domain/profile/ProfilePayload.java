package com.nabagagem.connectbe.domain.profile;

import com.nabagagem.connectbe.domain.rating.ProfileRatingPayload;
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
        UUID parentId,
        PersonalInfo personalInfo,
        Double averageStars,
        Set<SkillReadPayload> skills,
        Set<CertificationPayload> certifications,
        ProfileMetrics profileMetrics,
        ProfileBio bio,
        Map<DayOfWeek, AvailabilityType> availabilities,
        ProfileRatingPayload myRating, List<ProfileRatingPayload> lastRatings) {
}
