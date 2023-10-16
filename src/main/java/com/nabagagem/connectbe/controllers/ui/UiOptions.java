package com.nabagagem.connectbe.controllers.ui;

import com.nabagagem.connectbe.domain.bid.BidDirection;
import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.job.JobFrequency;
import com.nabagagem.connectbe.domain.job.JobRequiredAvailability;
import com.nabagagem.connectbe.domain.job.JobSize;
import com.nabagagem.connectbe.domain.job.JobStatus;
import com.nabagagem.connectbe.domain.profile.AvailabilityType;
import com.nabagagem.connectbe.domain.profile.WorkingMode;
import com.nabagagem.connectbe.entities.BidStatus;
import com.nabagagem.connectbe.entities.NotificationType;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.nabagagem.connectbe.entities.ProfileSkill.SkillLevel;

public record UiOptions(
        Map<AvailabilityType, String> availabilityTypes,
        Map<JobSize, String> jobSizes,
        Map<JobFrequency, String> jobFrequencies,
        Map<WorkingMode, String> jobModes,
        Map<JobRequiredAvailability, String> jobRequiredAvailabilities,
        Map<JobStatus, String> jobStatuses,
        Map<JobCategory, String> profileCategories,
        Map<WorkingMode, String> workingModes,
        Map<BidDirection, String> bidDirections,
        Map<BidStatus, String> bidStatuses,
        Map<NotificationType, String> notificationTypes,
        Map<SkillLevel, String> skillLevels,
        Set<String> skills,
        Map<Locale, String> languages
) {
}
