package com.nabagagem.connectbe.controllers.reports;

import com.nabagagem.connectbe.domain.CreateProfileReportPayload;
import com.nabagagem.connectbe.domain.PatchProfilePayload;
import com.nabagagem.connectbe.domain.ReportItemPayload;
import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.mappers.ProfileReportMapper;
import com.nabagagem.connectbe.services.profile.ProfileReportService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile-reports")
public class ProfileReportController {
    private final ProfileReportService profileReportService;
    private final ProfileReportMapper profileReportMapper;

    @PostMapping
    public ResourceRef post(@RequestBody CreateProfileReportPayload payload) {
        return new ResourceRef(
                profileReportService.create(payload).getId().toString());
    }

    @PatchMapping("/{id}")
    //Profile needs to be admin
    public void patch(@RequestBody PatchProfilePayload patchProfilePayload,
                      @PathVariable UUID id) {
        profileReportService.update(id, patchProfilePayload);
    }

    @DeleteMapping("/{id}")
    //Profile needs to be admin
    public void delete(@PathVariable UUID id) {
        profileReportService.delete(id);
    }

    @GetMapping
    //Profile needs to be admin
    public Page<ReportItemPayload> list(Pageable pageable) {
        return profileReportService.list(pageable)
                .map(profileReportMapper::toItemPayload);
    }

    @GetMapping("/{id}")
    //Profile needs to be admin
    public ResponseEntity<ReportItemPayload> get(@PathVariable UUID id) {
        return profileReportService.get(id)
                .map(profileReportMapper::toItemPayload)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
