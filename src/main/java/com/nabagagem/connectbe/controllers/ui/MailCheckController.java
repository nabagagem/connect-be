package com.nabagagem.connectbe.controllers.ui;

import com.nabagagem.connectbe.services.profile.ProfileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MailCheckController {
    private final ProfileService profileService;

    @GetMapping("/api/v1/ui/email")
    public void checkEmail(@RequestParam @Valid @Email String email) {
        profileService.failIfEmailExists(email);
    }

}
