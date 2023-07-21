package com.nabagagem.connectbe.services.jobs;

import com.nabagagem.connectbe.repos.JobRepo;
import com.nabagagem.connectbe.services.LoggedUserIdTrait;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class JobAuthService implements LoggedUserIdTrait {
    private final JobRepo jobRepo;

    public void failIfUnauthorized(UUID jobId) {
        if (!jobRepo.existsByOwnerIdAndId(
                loggedUser().orElseThrow(),
                jobId
        )) {
            throw new AccessDeniedException("Unauthorized");
        }
    }
}
