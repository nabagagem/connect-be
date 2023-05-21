package com.nabagagem.connectbe.controllers.ui;

import com.nabagagem.connectbe.controllers.ui.UiSupportController.CategoryTree;
import com.nabagagem.connectbe.domain.*;
import com.nabagagem.connectbe.entities.BidStatus;
import com.nabagagem.connectbe.entities.NotificationType;
import com.nabagagem.connectbe.entities.ProfileCategory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.nabagagem.connectbe.entities.ProfileSkill.SkillLevel;

public record UiOptions(
        Map<AvailabilityType, String> availabilityTypes,
        List<CategoryTree> jobCategories,
        Map<JobSize, String> jobSizes,
        Map<JobFrequency, String> jobFrequencies,
        Map<JobMode, String> jobModes,
        Map<JobRequiredAvailability, String> jobRequiredAvailabilities,
        Map<JobStatus, String> jobStatuses,
        Map<ProfileCategory, String> profileCategories,
        Map<WorkingMode, String> workingModes,
        Map<BidDirection, String> bidDirections,
        Map<BidStatus, String> bidStatuses,
        Map<NotificationType, String> notificationTypes,
        Map<SkillLevel, String> skillLevels,
        Set<String> skills
) {
}
