package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.repos.ThreadRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class ThreadAuthService implements UnwrapLoggedUserIdTrait {
    private final ThreadRepo threadRepo;

    public void failIfUnableToDelete(UUID threadId) {
        failIfUnableToRead(threadId);
    }

    public void failIfUnableToRead(UUID threadId) {
        UUID loggedUserId = unwrapLoggedUserId().orElseThrow();
        log.info("Checking permissions on thread {} for user {}", threadId, loggedUserId);
        if (!threadRepo.existsByIdAndUsers(threadId, loggedUserId)) {
            throw new AccessDeniedException("Unauthorized");
        }
    }

    public void failIfUnableToWrite(UUID threadId) {
        failIfUnableToRead(threadId);
    }
}
