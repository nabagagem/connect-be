package com.nabagagem.connectbe.services.events;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.ProfileType;
import com.nabagagem.connectbe.repos.ProfileRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class EventAuthService {
    private final ProfileRepo profileRepo;

    public void failIfUnableToManage(Principal principal) {
        profileRepo.findById(UUID.fromString(principal.getName()))
                .map(ConnectProfile::getProfileType)
                .filter(ProfileType.ADMIN::equals)
                .ifPresentOrElse(profileType -> log.info("Admin user checked: {}", principal.getName()),
                        () -> {
                            log.warn("Event create attempt with non admin user: {}", principal.getName());
                            throw new AccessDeniedException("Unauthorized");
                        });
    }
}
