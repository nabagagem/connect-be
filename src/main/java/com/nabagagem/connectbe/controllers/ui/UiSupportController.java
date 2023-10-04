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
import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.repos.SkillRepo;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.nabagagem.connectbe.entities.ProfileSkill.SkillLevel;

@RestController
@AllArgsConstructor
public class UiSupportController {
    private final SkillRepo skillRepo;
    private final MessageSource messageSource;

    @GetMapping("/api/v1/ui/options")
    public UiOptions get() {
        return new UiOptions(
                translateList(AvailabilityType.values()),
                translateList(JobSize.values()),
                translateList(JobFrequency.values()),
                translateList(WorkingMode.values()),
                translateList(JobRequiredAvailability.values()),
                translateList(JobStatus.values()),
                translateList(JobCategory.values()),
                translateList(WorkingMode.values()),
                translateList(BidDirection.values()),
                translateList(BidStatus.values()),
                translateList(NotificationType.values()),
                translateList(SkillLevel.values()),
                StreamSupport.stream(skillRepo.findAll().spliterator(), false)
                        .map(Skill::getName)
                        .collect(Collectors.toSet())
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
