package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.JobPatchPayload;
import com.nabagagem.connectbe.domain.JobPayload;
import com.nabagagem.connectbe.domain.JobStatus;
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
    private final JobIndexService jobIndexService;

    public Job create(@Valid JobPayload jobPayload, UUID ownerId) {
        Job job = jobMapper.map(jobPayload);
        job.setJobStatus(Optional.ofNullable(jobPayload.jobStatus())
                .orElse(JobStatus.DRAFT));
        reloadSkills(job, jobPayload);
        job.setOwner(profileRepo.findById(ownerId)
                .orElseThrow());
        return save(job);
    }

    public Job save(Job job) {
        job.setKeywords(jobIndexService.extractFrom(job));
        return jobRepo.save(job);
    }

    private void reloadSkills(Job job, JobPayload jobPayload) {
        Optional.ofNullable(jobPayload.requiredSkills())
                .map(this::toSkills)
                .ifPresent(job::setRequiredSkills);
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

    public void delete(UUID id) {
        jobRepo.deleteById(id);
    }

    public void update(UUID jobId, JobPayload jobPayload) {
        Job job = jobRepo.findById(jobId).orElseThrow();
        jobMapper.map(job, jobPayload);
        reloadSkills(job, jobPayload);
        save(job);
    }

    public void patch(UUID id, JobPatchPayload jobPatchPayload) {
        Job job = jobRepo.findById(id).orElseThrow();
        job.setJobStatus(jobPatchPayload.jobStatus());
        save(job);
    }
}
