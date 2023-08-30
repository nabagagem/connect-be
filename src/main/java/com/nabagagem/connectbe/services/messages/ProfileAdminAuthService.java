package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.entities.ProfileType;
import com.nabagagem.connectbe.repos.ProfileRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ProfileAdminAuthService {
    private final ProfileRepo profileRepo;

    public void failIfUnauthorizedToPatch(UUID id) {
        profileRepo.getProfileTypeFor(id)
                .filter(ProfileType.ADMIN::equals)
                .orElseThrow(() -> new AccessDeniedException("Unauthorized"));
    }
}
