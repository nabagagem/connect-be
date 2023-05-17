package com.nabagagem.connectbe.controllers.ui;

import com.nabagagem.connectbe.domain.AvailabilityType;
import com.nabagagem.connectbe.domain.BidDirection;
import com.nabagagem.connectbe.domain.JobCategory;
import com.nabagagem.connectbe.domain.JobFrequency;
import com.nabagagem.connectbe.domain.JobMode;
import com.nabagagem.connectbe.domain.JobRequiredAvailability;
import com.nabagagem.connectbe.domain.JobSize;
import com.nabagagem.connectbe.domain.JobStatus;
import com.nabagagem.connectbe.domain.WorkingMode;
import com.nabagagem.connectbe.entities.BidStatus;
import com.nabagagem.connectbe.entities.NotificationType;
import com.nabagagem.connectbe.entities.ProfileCategory;
import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.repos.SkillRepo;
import lombok.AllArgsConstructor;
import org.mapstruct.ap.internal.util.Collections;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@AllArgsConstructor
public class UiSupportController {
    private final SkillRepo skillRepo;
    private final MessageSource messageSource;

    @GetMapping("/api/v1/ui/options")
    public UiOptions get() {
        return new UiOptions(
                translateList(AvailabilityType.values()),
                translateCategories(JobCategory.values()),
                translateList(JobSize.values()),
                translateList(JobFrequency.values()),
                translateList(JobMode.values()),
                translateList(JobRequiredAvailability.values()),
                translateList(JobStatus.values()),
                translateList(ProfileCategory.values()),
                translateList(WorkingMode.values()),
                translateList(BidDirection.values()),
                translateList(BidStatus.values()),
                translateList(NotificationType.values()),
                StreamSupport.stream(skillRepo.findAll().spliterator(), false)
                        .map(Skill::getName)
                        .collect(Collectors.toSet())
        );
    }

    private List<CategoryTree> translateCategories(JobCategory[] values) {
        return Collections.join(
                Arrays.stream(values)
                        .filter(jobCategory -> !jobCategory.getSubCategories().isEmpty())
                        .map(jobCategory -> new CategoryTree(
                                translate(jobCategory),
                                jobCategory.getSubCategories()
                                        .stream().map(this::translate)
                                        .collect(Collectors.toSet())
                        )).collect(Collectors.toList()),
                Arrays.stream(values)
                        .filter(jobCategory -> jobCategory.getSubCategories().isEmpty())
                        .map(jobCategory -> new CategoryTree(
                                translate(jobCategory),
                                Set.of()
                        )).collect(Collectors.toList())
        );
    }

    private <T> Map<T, String> translateList(T[] values) {
        return Arrays.stream(values)
                .map(this::translate)
                .collect(Collectors.toMap(
                        IdNameCombo::key,
                        IdNameCombo::label
                ));
    }

    private <T> IdNameCombo<T> translate(T toBeTranslated) {
        return new IdNameCombo<>(
                toBeTranslated,
                messageSource.getMessage(
                        toBeTranslated.toString(),
                        null, LocaleContextHolder.getLocale())
        );
    }

    private record IdNameCombo<T>(
            T key,
            String label
    ) {
    }

    record CategoryTree(
            IdNameCombo<JobCategory> category,
            Set<IdNameCombo<JobCategory>> subCategories
    ) {
    }
}
