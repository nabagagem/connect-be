package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.repos.JobRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class JobAuthService implements UnwrapLoggedUserIdTrait {
    private final JobRepo jobRepo;

    public void failIfUnauthorized(UUID jobId) {
        if (!jobRepo.existsByOwnerIdAndId(
                unwrapLoggedUserId().orElseThrow(),
                jobId
        )) {
            throw new AccessDeniedException("Unauthorized");
        }
    }
}
