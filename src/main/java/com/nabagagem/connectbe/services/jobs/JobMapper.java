package com.nabagagem.connectbe.services.jobs;

import com.nabagagem.connectbe.domain.job.JobPayload;
import com.nabagagem.connectbe.domain.job.JobSearchItem;
import com.nabagagem.connectbe.domain.notification.NotificationCommand;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.entities.Notification;
import com.nabagagem.connectbe.entities.Skill;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface JobMapper {
    @Mapping(target = "requiredSkills", ignore = true)
    Job map(JobPayload jobPayload);

    @Mapping(target = "ownerId", source = "owner.id")
    JobPayload toDto(Job job);

    default Set<String> mapSkill(Set<Skill> value) {
        return Optional.ofNullable(value)
                .map(__ -> value.stream().map(Skill::getName)
                        .collect(Collectors.toSet()))
                .orElseGet(Collections::emptySet);
    }

    @Mapping(target = "profile.id", source = "owner.id")
    @Mapping(target = "profile.publicName", source = "owner.personalInfo.publicName")
    @Mapping(target = "createdAt", source = "audit.createdAt")
    JobSearchItem toSearchItem(Job job);

    Notification toEntity(NotificationCommand notificationCommand);

    NotificationCommand toDto1(Notification notification);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Notification partialUpdate(NotificationCommand notificationCommand, @MappingTarget Notification notification);

    @Mapping(target = "requiredSkills", ignore = true)
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void map(@MappingTarget Job job, JobPayload jobPayload);
}
