package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.PatchThreadPayload;
import com.nabagagem.connectbe.domain.exceptions.ThreadBlockedByAnotherUser;
import com.nabagagem.connectbe.entities.Audit;
import com.nabagagem.connectbe.entities.Thread;
import com.nabagagem.connectbe.entities.ThreadStatus;
import com.nabagagem.connectbe.repos.ThreadRepo;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class ThreadAuthService implements LoggedUserIdTrait {
    private final ThreadRepo threadRepo;

    public void failIfUnableToDelete(UUID threadId) {
        failIfUnableToRead(threadId);
    }

    public void failIfUnableToRead(UUID threadId) {
        UUID loggedUserId = loggedUser().orElseThrow();
        log.info("Checking permissions on thread {} for user {}", threadId, loggedUserId);
        if (!threadRepo.existsByIdAndUsers(threadId, loggedUserId)) {
            throw new AccessDeniedException("Unauthorized");
        }
    }

    public void failIfUnableToUpdate(UUID threadId, @Valid PatchThreadPayload patchThreadPayload) {
        Thread thread = threadRepo.findById(threadId).orElseThrow();
        UUID loggedUserId = loggedUser().orElseThrow();
        String modifiedBy = Optional.ofNullable(thread.getAudit())
                .map(Audit::getModifiedBy)
                .orElse("");
        log.info("Thread update attempt: Current status: {}, new status: {}, loggedUserId: {}, last modified by: {}",
                thread.getStatus(), patchThreadPayload.status(),
                loggedUserId, modifiedBy);
        if (thread.isBlocked() && patchThreadPayload.status() == ThreadStatus.OPEN
                && !modifiedBy.equals(loggedUserId.toString())) {
            throw new ThreadBlockedByAnotherUser();
        }
    }
}
