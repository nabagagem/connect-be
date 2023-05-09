package com.nabagagem.connectbe.controllers;

import com.nabagagem.connectbe.controllers.UiSupportController.CategoryTree;
import com.nabagagem.connectbe.domain.*;
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
        Set<String> skills
) {
}
