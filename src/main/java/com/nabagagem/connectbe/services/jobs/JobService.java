package com.nabagagem.connectbe.services.jobs;

import com.nabagagem.connectbe.domain.job.JobPatchPayload;
import com.nabagagem.connectbe.domain.job.JobPayload;
import com.nabagagem.connectbe.domain.job.JobStatus;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.repos.JobRepo;
import com.nabagagem.connectbe.services.profile.ProfileService;
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
    private final JobIndexService jobIndexService;
    private final JobFileService jobFileService;
    private final ProfileService profileService;

    public Job create(@Valid JobPayload jobPayload, UUID ownerId) {
        Job job = jobMapper.map(jobPayload);
        job.setJobStatus(JobStatus.PUBLISHED);
        reloadSkills(job, jobPayload);
        job.setOwner(profileService.findOrCreate(ownerId));
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
        jobFileService.deleteAllForJob(id);
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

    public void deleteForUser(UUID id) {
        jobRepo.findByOwnerId(id)
                .stream().map(Job::getId)
                .forEach(this::delete);
    }
}
