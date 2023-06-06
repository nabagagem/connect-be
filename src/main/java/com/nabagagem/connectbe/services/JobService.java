package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.JobPayload;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.repos.JobRepo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.profile.SkillService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class JobService {
    private final JobRepo jobRepo;
    private final SkillService skillService;
    private final JobMapper jobMapper;
    private final ProfileRepo profileRepo;

    public Job create(@Valid JobPayload jobPayload) {
        Job job = jobMapper.map(jobPayload);
        Optional.ofNullable(jobPayload.requiredSkills())
                .map(this::toSkills)
                .ifPresent(job::setRequiredSkills);
        job.setOwner(profileRepo.findById(UUID.fromString(jobPayload.ownerId()))
                .orElseThrow());
        return jobRepo.save(job);
    }

    private Set<Skill> toSkills(Set<String> skills) {
        return skills.stream()
                .map(skillService::findOrCreate)
                .collect(Collectors.toSet());
    }

    public Optional<JobPayload> getJob(String id) {
        return jobRepo.findById(UUID.fromString(id))
                .map(jobMapper::toDto);
    }
}
