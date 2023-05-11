package com.nabagagem.connectbe.controllers.ui;

import com.nabagagem.connectbe.controllers.ui.UiSupportController.CategoryTree;
import com.nabagagem.connectbe.domain.AvailabilityType;
import com.nabagagem.connectbe.domain.BidDirection;
import com.nabagagem.connectbe.domain.JobFrequency;
import com.nabagagem.connectbe.domain.JobMode;
import com.nabagagem.connectbe.domain.JobRequiredAvailability;
import com.nabagagem.connectbe.domain.JobSize;
import com.nabagagem.connectbe.domain.JobStatus;
import com.nabagagem.connectbe.domain.WorkingMode;
import com.nabagagem.connectbe.entities.BidStatus;
import com.nabagagem.connectbe.entities.NotificationType;
import com.nabagagem.connectbe.entities.ProfileCategory;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
        Set<String> skills
) {
}
