package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.repos.ReactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ReactAuthService {
    private final ReactionRepository reactionRepository;
    private final LoginHelper loginHelper;

    public void failIfUnableToDelete(UUID reactionId) {
        if (!reactionRepository.existsByIdAndOwner(
                reactionId,
                loginHelper.getLoggedUserId().toString()
        )) {
            throw new AccessDeniedException("Unauthorized");
        }
    }
}
