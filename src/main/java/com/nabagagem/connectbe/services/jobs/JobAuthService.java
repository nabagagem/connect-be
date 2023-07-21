package com.nabagagem.connectbe.services.jobs;

import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.repos.JobRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class JobAuthService {
    private final JobRepo jobRepo;
    private final LoginHelper loginHelper;

    public void failIfUnauthorized(UUID jobId) {
        if (!jobRepo.existsByOwnerIdAndId(
                loginHelper.loggedUser().orElseThrow(),
                jobId
        )) {
            throw new AccessDeniedException("Unauthorized");
        }
    }
}
