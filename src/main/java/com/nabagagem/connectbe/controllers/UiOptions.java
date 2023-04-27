package com.nabagagem.connectbe.controllers;

import com.nabagagem.connectbe.controllers.UiSupportController.CategoryTree;
import com.nabagagem.connectbe.domain.AvailabilityType;
import com.nabagagem.connectbe.domain.JobFrequency;
import com.nabagagem.connectbe.domain.JobMode;
import com.nabagagem.connectbe.domain.JobRequiredAvailability;
import com.nabagagem.connectbe.domain.JobSize;
import com.nabagagem.connectbe.domain.JobStatus;
import com.nabagagem.connectbe.domain.WorkingMode;
import com.nabagagem.connectbe.entities.ProfileCategory;

import java.util.Map;
import java.util.Set;

public record UiOptions(
        Map<AvailabilityType, String> availabilityTypes,
        java.util.List<CategoryTree> jobCategories,
        Map<JobSize, String> jobSizes,
        Map<JobFrequency, String> jobFrequencies,
        Map<JobMode, String> jobModes,
        Map<JobRequiredAvailability, String> jobRequiredAvailabilities,
        Map<JobStatus, String> jobStatuses,
        Map<ProfileCategory, String> profileCategories,
        Map<WorkingMode, String> workingModes,
        Set<String> skills
) {
}
