package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.ProfileSearchItemPayload;
import com.nabagagem.connectbe.domain.ProfileSearchParams;
import com.nabagagem.connectbe.services.ProfileSearchService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileSearchController {
    private final ProfileSearchService profileSearchService;

    @GetMapping
    public Page<ProfileSearchItemPayload> list(@Valid ProfileSearchParams profileSearchParams,
                                               @PageableDefault(size = 10) Pageable pageable) {
        return profileSearchService.searchFor(profileSearchParams, pageable);
    }

}
