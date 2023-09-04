package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.services.profile.DeleteUserService;
import com.nabagagem.connectbe.services.profile.ProfileAuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{id}")
public class DeleteProfileController {
    private final DeleteUserService deleteUserService;
    private final ProfileAuthService profileAuthService;

    @DeleteMapping
    public void delete(@PathVariable UUID id) {
        profileAuthService.failIfNotCurrentProfile(id);
        deleteUserService.delete(id);
    }
}
