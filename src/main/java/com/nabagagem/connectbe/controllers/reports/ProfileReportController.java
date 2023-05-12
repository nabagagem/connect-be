package com.nabagagem.connectbe.controllers.reports;

import com.nabagagem.connectbe.domain.CreateProfileReportPayload;
import com.nabagagem.connectbe.domain.PatchProfilePayload;
import com.nabagagem.connectbe.domain.ReportItemPayload;
import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.mappers.ProfileReportMapper;
import com.nabagagem.connectbe.services.ProfileReportService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile-reports")
public class ProfileReportController {
    private final ProfileReportService profileReportService;
    private final ProfileReportMapper profileReportMapper;

    @PostMapping
    public ResourceRef post(@RequestBody CreateProfileReportPayload createProfileReportPayload) {
        return new ResourceRef(
                profileReportService.create(createProfileReportPayload).getId().toString());
    }

    @PatchMapping("/{id}")
    public void patch(@RequestBody PatchProfilePayload patchProfilePayload,
                      @PathVariable UUID id) {
        profileReportService.update(id, patchProfilePayload);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        profileReportService.delete(id);
    }

    @GetMapping
    public Page<ReportItemPayload> list(Pageable pageable) {
        return profileReportService.list(pageable)
                .map(profileReportMapper::toItemPayload);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportItemPayload> get(@PathVariable UUID id) {
        return profileReportService.get(id)
                .map(profileReportMapper::toItemPayload)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
