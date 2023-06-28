package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.repos.MessageRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class MessageAuthService implements UnwrapLoggedUserIdTrait {
    private final MessageRepo messageRepo;
    private final ThreadAuthService threadAuthService;

    public void failIfUnableToWriteOnThread(UUID threadId) {
        threadAuthService.failIfUnableToRead(threadId);
    }

    public void failIfUnableToDelete(UUID id) {
        String loggedUserId = unwrapLoggedUserId().map(UUID::toString)
                .orElseThrow();
        if (!messageRepo.existsByIdAndCreator(id, loggedUserId)) {
            throw new AccessDeniedException("Unauthorized");
        }
    }

    public void failIfUnableToPatch(UUID id) {
        failIfUnableToDelete(id);
    }

    public void failIfUnableToRead(UUID id) {
        if (!messageRepo.isUserOnThread(id, getUserIdOrFail())) {
            throw new AccessDeniedException("Unauthorized");
        }
    }
}
