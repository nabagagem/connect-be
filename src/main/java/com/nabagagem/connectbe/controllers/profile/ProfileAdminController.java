package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.profile.AdminProfileCommand;
import com.nabagagem.connectbe.services.messages.ProfileAdminService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{id}")
public class ProfileAdminController {
    private final ProfileAdminService profileAdminService;

    @PutMapping("/admin")
    public void patch(@PathVariable UUID id,
                      @RequestBody @Valid AdminProfileCommand adminProfileCommand) {
        profileAdminService.patch(id, adminProfileCommand);
    }
}
