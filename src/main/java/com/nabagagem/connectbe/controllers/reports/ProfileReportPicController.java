package com.nabagagem.connectbe.controllers.reports;

import com.nabagagem.connectbe.controllers.MediaControllerHelper;
import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.domain.report.ReportPicItem;
import com.nabagagem.connectbe.entities.ReportPic;
import com.nabagagem.connectbe.services.profile.ProfileReportPicService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile-reports/{reportId}/pics")
public class ProfileReportPicController {
    private final ProfileReportPicService profileReportPicService;
    private final MediaControllerHelper mediaControllerHelper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceRef upload(@RequestParam MultipartFile file,
                              @PathVariable UUID reportId) {
        mediaControllerHelper.validateFile(file);
        return new ResourceRef(profileReportPicService.create(reportId, file).getId().toString());
    }

    @GetMapping
    public List<ReportPicItem> list(@PathVariable UUID reportId) {
        return profileReportPicService.list(reportId)
                .stream()
                .map(ReportPic::getId)
                .map(ReportPicItem::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> get(@PathVariable UUID id,
                                      @PathVariable UUID reportId) {
        return profileReportPicService.getMediaByPicId(id)
                .map(mediaControllerHelper::toResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id,
                       @PathVariable UUID reportId) {
        profileReportPicService.delete(id);
    }
}
