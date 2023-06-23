package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.repos.ThreadRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ThreadAuthService implements UnwrapLoggedUserIdTrait {
    private final ThreadRepo threadRepo;

    public void failIfUnableToDelete(UUID threadId) {
        if (!threadRepo.existsByIdAndSenderId(threadId, unwrapLoggedUserId().orElseThrow())) {
            throw new AccessDeniedException("Unauthorized");
        }
    }

    public void failIfUnableToRead(UUID threadId) {
        if (!threadRepo.existsByIdAndUsers(threadId, unwrapLoggedUserId().orElseThrow())) {
            throw new AccessDeniedException("Unauthorized");
        }
    }
}
