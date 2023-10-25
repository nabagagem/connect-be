package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.services.profile.MyProfileService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/my-profile")
public class MyProfileController {

    private final MyProfileService myProfileService;

    @GetMapping
    public ProfilePayload getMyProfileInfo(Principal principal) {
        return myProfileService.getMyProfileFor(UUID.fromString(principal.getName()));
    }
}
