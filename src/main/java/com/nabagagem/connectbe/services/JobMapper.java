package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.JobPayload;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.entities.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
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
}