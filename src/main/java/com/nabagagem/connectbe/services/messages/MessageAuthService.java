package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.domain.exceptions.MessageCannotBeRead;
import com.nabagagem.connectbe.domain.messages.MessagePatchPayload;
import com.nabagagem.connectbe.repos.MessageRepo;
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
public class MessageAuthService {
    private final MessageRepo messageRepo;
    private final ThreadAuthService threadAuthService;
    private final LoginHelper loginHelper;

    public void failIfUnableToWriteOnThread(UUID threadId) {
        threadAuthService.failIfUnableToRead(threadId);
    }

    public void failIfUnableToDelete(UUID id) {
        String loggedUserId = loginHelper.loggedUser().map(UUID::toString)
                .orElseThrow();
        if (!messageRepo.existsByIdAndCreator(id, loggedUserId)) {
            throw new AccessDeniedException("Unauthorized");
        }
    }

    public void failIfUnableToPatch(UUID id, @Valid MessagePatchPayload messagePatchPayload) {
        Optional.ofNullable(messagePatchPayload.read())
                .ifPresent(__ -> failIfNotRecipient(id));
        Optional.ofNullable(messagePatchPayload.text())
                .ifPresent(__ -> failIfUnableToDelete(id));
    }

    private void failIfNotRecipient(UUID id) {
        UUID loggedUserId = loginHelper.getLoggedUserId();
        log.info("Checking if {} is the recipient of the message {}", loggedUserId, id);
        if (!messageRepo.isTheRecipientOf(id, loggedUserId, loggedUserId.toString())) {
            throw new MessageCannotBeRead();
        }
    }

    public void failIfUnableToRead(UUID id) {
        if (!messageRepo.isUserOnThread(id, loginHelper.getLoggedUserId())) {
            throw new AccessDeniedException("Unauthorized");
        }
    }

    public void failIfUnableToReact(UUID messageId) {
        failIfUnableToRead(messageId);
    }
}
