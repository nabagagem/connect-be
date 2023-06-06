package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.ReportPic;
import com.nabagagem.connectbe.repos.ProfileReportRepository;
import com.nabagagem.connectbe.repos.ReportPicRepository;
import com.nabagagem.connectbe.services.MediaService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class ProfileReportPicService {
    private final MediaService mediaService;
    private final ProfileReportRepository profileReportRepository;
    private final ReportPicRepository reportPicRepository;

    public ReportPic create(UUID reportId, MultipartFile file) {
        return reportPicRepository.save(
                ReportPic.builder()
                        .profileReport(profileReportRepository.findById(reportId).orElseThrow())
                        .media(mediaService.upload(file))
                        .build()
        );
    }

    public List<ReportPic> list(UUID reportId) {
        return reportPicRepository.findByProfileReportId(reportId);
    }

    public void delete(UUID id) {
        reportPicRepository.findById(id)
                .ifPresent(reportPic -> {
                    mediaService.delete(reportPic.getMedia());
                    reportPicRepository.delete(reportPic);
                });
    }

    public Optional<Media> getMediaByPicId(UUID id) {
        return reportPicRepository.findMediaByPicId(id);
    }
}
