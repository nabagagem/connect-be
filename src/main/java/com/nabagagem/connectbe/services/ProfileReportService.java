package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.CreateProfileReportPayload;
import com.nabagagem.connectbe.domain.PatchProfilePayload;
import com.nabagagem.connectbe.domain.exceptions.ReportNotFoundException;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.ProfileReport;
import com.nabagagem.connectbe.mappers.ProfileReportMapper;
import com.nabagagem.connectbe.resources.JobRepo;
import com.nabagagem.connectbe.resources.ProfileRepo;
import com.nabagagem.connectbe.resources.ProfileReportRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class ProfileReportService {
    private final ProfileReportRepository profileReportRepository;
    private final ProfileRepo profileRepo;
    private final JobRepo jobRepo;
    private final ProfileReportMapper profileReportMapper;

    public ProfileReport create(CreateProfileReportPayload payload) {
        ProfileReport report = profileReportMapper.toEntity(payload);
        report.setReporter(getUserFrom(payload.reporterProfileId()));
        Optional.ofNullable(payload.targetJobId())
                .flatMap(jobRepo::findById)
                .ifPresentOrElse(job -> {
                            report.setTargetJob(job);
                            report.setTargetProfile(job.getOwner());
                        }, () -> report.setTargetProfile(
                                getUserFrom(
                                        payload.targetProfileId())
                        )
                );
        return profileReportRepository.save(report);
    }

    private ConnectProfile getUserFrom(UUID userId) {
        return profileRepo.findById(userId).orElseThrow();
    }

    public void update(UUID id, PatchProfilePayload patchProfilePayload) {
        profileReportRepository.findById(id)
                .ifPresentOrElse(profileReport -> {
                    profileReport.setReportAction(patchProfilePayload.reportAction());
                    profileReport.setReportStatus(patchProfilePayload.reportStatus());
                    profileReportRepository.save(profileReport);
                }, () -> {
                    throw new ReportNotFoundException();
                });
    }

    public void delete(UUID id) {
        profileReportRepository.deleteById(id);
    }

    public Page<ProfileReport> list(Pageable pageable) {
        return profileReportRepository.findPage(pageable);
    }

    public Optional<ProfileReport> get(UUID id) {
        return profileReportRepository.findFullBy(id);
    }
}
